(ns migae-test.blobstore-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]))

(defroutes blobstore-routes
  (GET "/blobstore/:arg" [arg]
    (str (format
          "<h1>Ohayo %s from migae-test.blobstore-impl servlet path /blobstore/*!</h1>"
          arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>Page not found</h1>"))

(def blobstore-handler
  (-> #'blobstore-routes
      wrap-params
      wrap-file-info
      ))

