(ns appengine-magic.jetty
  (:use [leiningen.core.project :as proj])
   ;[appengine-magic.jetty.servlet :only [servlet]])
  (:refer-clojure :exclude [read])
  (:import org.mortbay.jetty.handler.ContextHandlerCollection
           [org.mortbay.jetty Server Handler]
           javax.servlet.http.HttpServlet
           javax.servlet.Filter
           [org.mortbay.jetty.servlet Context ServletHolder FilterHolder]))

(defn read-proj []
  (proj/read))

(defn- proxy-multihandler
  "Returns a Jetty Handler implementation for the given map of relative URLs to
   handlers. Each handler may be a Ring handler or an HttpServlet instance."
  [filters all-handlers]
  (let [all-contexts (ContextHandlerCollection.)
        context (Context. all-contexts "/" Context/SESSIONS)]
    (doseq [[url filter-objs] filters]
      (let [filter-objs (if (sequential? filter-objs) filter-objs [filter-objs])]
        (doseq [filter-obj filter-objs]
          (.addFilter context (FilterHolder. filter-obj) url Handler/ALL))))
    (doseq [[relative-url url-handler] all-handlers]
      (.addServlet context (ServletHolder. url-handler) relative-url))
    all-contexts))

;; jetty/start
;; = ring.adapter.jetty/run-jetty
(defn #^Server start [filter-map servlet-map &
                      {:keys [port join?] :or {port 8080 join? false}}]
  (let [server (Server. port)]
    (doto server
      (.setHandler (proxy-multihandler filter-map servlet-map))
      (.start))
    (when join? (.join server))
    server))

;; jetty/stop
(defn stop [#^Server server]
  (.stop server))

;; (defn start [project servlet]
;;   ;; & {:keys [port join? high-replication in-memory]
;;     ;;                  :or {port 8080, join? false,
;;   ;;                       high-replication false, in-memory false}}]
;;   (let [war-root (java.io.File. (:war-root servlet))
;;         ;; setup HttpServlet and its service method
;;         ;; handler-servlet (servlet (:handler servlet))]
;;         handler-servlet (make-servlet servlet)]
;;     (appengine-init war-root port high-replication in-memory)
;;     (reset!
;;      *server*
;;      (jetty/start
;;       {"/*" [(make-appengine-request-environment-filter)
;;              (com.google.apphosting.utils.servlet.TransactionCleanupFilter.)
;;              (com.google.appengine.api.blobstore.dev.ServeBlobFilter.)]}
;;       {"/" handler-servlet
;;        ;; These mappings are from webdefault.xml in appengine-local-runtime-*.jar.
;;        "/_ah/admin" (com.google.apphosting.utils.servlet.DatastoreViewerServlet.)
;;        "/_ah/xmppHead" (org.apache.jsp.ah.xmppHead_jsp.)}
;;       :port port
;;       :join? join?))))

;; ;; ae/stop
;; (defn stop []
;;   (when-not (nil? @*server*)
;;     (appengine-clear)
;;     (jetty/stop @*server*)
;;     (reset! *server* nil)))

;; ;; ae/serve
;; (defn serve [servlet & {:keys [port high-replication in-memory]
;;                               :or {port 8080, high-replication false, in-memory false}}]
;;   (stop)
;;   (start servlet :port port :high-replication high-replication :in-memory in-memory))

