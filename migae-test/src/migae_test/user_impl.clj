(ns migae-test.user-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]))

(defroutes user-routes
  (GET "/user/:arg" [arg]
    (str (format "<h1>Ohayo %s from migae-test.user-impl servlet path /user/*!</h1>"
                 arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>Page not found</h1>"))

(def user-handler
  (-> #'user-routes
      wrap-params
      wrap-file-info
      ))

