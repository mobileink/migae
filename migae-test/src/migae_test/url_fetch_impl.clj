(ns migae-test.url-fetch-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]))

(defroutes url-fetch-routes
  (GET "/urlfetch/:arg" [arg]
    (str (format
          "<h1>Ohayo %s from migae-test.url-fetch-impl servlet path /url/*!</h1>"
          arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>Page not found</h1>"))

(def url-fetch-handler
  (-> #'url-fetch-routes
      wrap-params
      wrap-file-info
      ))

