(ns migae-test.blobstore-servlet
    (:gen-class :extends javax.servlet.http.HttpServlet)
    (:require [migae-test.blobstore-impl :as impl]
            [ring.util.servlet :as ring]))

(defn -service
  [this rqst resp]
    (let [request-map  (ring/build-request-map rqst)
          response-map (impl/blobstore-handler request-map)]
    (when response-map
      (ring/update-servlet-response resp response-map))))
