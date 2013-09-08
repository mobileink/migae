(ns migae-test.channel-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]
            [migae.migae-channel]
            [clojure.tools.logging :as log :only [debug info]]))


(defroutes channel-routes
  (GET "/channel/:arg" [arg]
    (str (format "<h1>Ohayo %s from migae-test.channel-impl servlet path /channel/*!</h1>"
                 arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>Page not found</h1>"))

(def channel-handler
  (-> #'channel-routes
      wrap-params
      wrap-file-info
      ))

