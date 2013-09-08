(ns migae-test.images-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]
            [migae.migae-images]
            [clojure.tools.logging :as log :only [debug info]]))


(defroutes images-routes
  (GET "/images/:arg" [arg]
    (str (format "<h1>Ohayo %s from migae-test.images-impl servlet path /images/*!</h1>"
                 arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>Page not found</h1>"))

(def images-handler
  (-> #'images-routes
      wrap-params
      wrap-file-info
      ))

