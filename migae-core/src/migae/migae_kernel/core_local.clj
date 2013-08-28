(in-ns 'migae.migae-kernel)

(use 'migae.kernel.local-env-helpers
     '[migae.kernel.servlet :only [servlet]]
;;     '[migae.kernel.swank :only [wrap-swank]]
     '[ring.middleware.file :only [wrap-file]]
     '[ring.middleware.file-info :only [wrap-file-info]])

(require '[clojure.string :as str]
         '[migae.kernel.jetty :as jetty]
         '[migae.kernel.blobstore-upload :as blobstore-upload])

(import java.io.File
        com.google.apphosting.api.ApiProxy)



;;; ----------------------------------------------------------------------------
;;; migae core API functions
;;; ----------------------------------------------------------------------------

;; this makes an assumption about current dir and about name of war
;; dir; better to use the project map for it.  also, this is called by
;; (the expansion of) def-appengine-app (aka defServlet) so it's
;; called at runtime by each servlet.  but it's a static value so it
;; can be supplied at server startup time.

(defn default-war-root []
  (-> (clojure.lang.RT/baseLoader)
      (.getResource ".")
      .getFile
      java.net.URLDecoder/decode
      (File. "../war")
      .getAbsolutePath))

(defn wrap-war-static [app, #^String war-root]
  (fn [req]
    (let [#^String uri (:uri req)]
      (if (.startsWith uri "/WEB-INF")
          (app req)
          ((wrap-file-info (wrap-file app war-root)) req)))))


;; NOT NEEDED
;;(defmacro def-migae-app [app-var-name handler & {:keys [war-root]}]
;; this is designed to feed into ae/start, ae-serve
;; i.e. tightly bound to that env in order to pass war-root etc.
;; better: just make a servlet
(defmacro defServlet [app-var-name handler & {:keys [war-root]}]
  `(def ~app-var-name
        (let [handler# ~handler
              war-root-arg# ~war-root
              war-root# (if (nil? war-root-arg#)
                            (default-war-root)
                            war-root-arg#)]
          {:handler (-> handler#
;;                        wrap-swank
                        (wrap-war-static war-root#))
           :war-root war-root#})))



;; this is just for the repl?  in prod, app-servlet var not used?
;; (ae/def-migae-app app-servlet #'request-handler)

;; result of this is used in prod
;; (defn -service [this request response]
;;   ((make-servlet-service-method app-servlet) this request response))

;; the idea of ring is to install ring routers/handlers via the
;; service method of the servlet api
