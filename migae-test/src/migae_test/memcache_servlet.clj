(ns migae-test.memcache-servlet
    (:gen-class :extends javax.servlet.http.HttpServlet)
    (:require [migae-test.memcache-impl :as impl]
            [ring.util.servlet :as ring]))

(defn -service
  [this rqst resp]
    (let [request-map  (ring/build-request-map rqst)
          response-map (impl/memcache-handler request-map)]
    (when response-map
      (ring/update-servlet-response resp response-map))))
