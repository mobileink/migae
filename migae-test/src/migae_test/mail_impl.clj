(ns migae-test.mail-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]))

(defroutes mail-routes
  (GET "/mail/:arg" [arg]
    (str (format "<h1>Ohayo %s from migae-test.mail-impl servlet path /mail/*!</h1>"
                 arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>Page not found</h1>"))

(def mail-handler
  (-> #'mail-routes
      wrap-params
      wrap-file-info
      ))

