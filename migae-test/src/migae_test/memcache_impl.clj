(ns migae-test.memcache-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]))

(defroutes memcache-routes
  (GET "/memcache/:arg" [arg]
    (str (format "<h1>Ohayo %s from migae-test.memcache-impl servlet path /memcache/*!</h1>"
                 arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>Page not found</h1>"))

(def memcache-handler
  (-> #'memcache-routes
      wrap-params
      wrap-file-info
      ))

