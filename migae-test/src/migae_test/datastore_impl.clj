(ns migae-test.datastore-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]
            [migae.migae-datastore]
            [clojure.tools.logging :as log :only [debug info]]))


(defroutes datastore-routes
  (GET "/datastore/:arg" [arg]
    (str (format "<h1>Ohayo %s from migae-test.datastore-impl servlet path /datastore/*!</h1>"
                 arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>Page not found</h1>"))

(def datastore-handler
  (-> #'datastore-routes
      wrap-params
      wrap-file-info
      ))

