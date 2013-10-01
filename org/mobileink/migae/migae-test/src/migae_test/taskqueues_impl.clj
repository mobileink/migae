(ns migae-test.taskqueues-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]
            [migae.migae-taskqueues]
            [clojure.tools.logging :as log :only [debug info]]))

(defroutes taskqueues-routes
  (GET "/taskqueues/:arg" [arg]
    (str (format "<h1>Ohayo %s from migae-test.taskqueues-impl servlet path /taskqueues/*!</h1>"
                 arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>Page not found</h1>"))

(def taskqueues-handler
  (-> #'taskqueues-routes
      wrap-params
      wrap-file-info
      ))

