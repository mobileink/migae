(ns migae-test.user-impl
  (:use compojure.core
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.file-info :only [wrap-file-info]])
  (:require [compojure.route :as route]
            [migae.migae-user :as aeu]
            [clojure.tools.logging :as log :only [debug info]]))

(defroutes user-routes

    (GET "/_ah/null" []
           {:status 200
            :headers {"Content-Type" "text/html"}
            :body (format "ok")})

    (GET "/_ah/login" [continue]
         ;; {uri :uri rq :request qp :query-params}
         (do
;           (log/info "/login served by login servlet")
           {:status 200
            :headers {"Content-Type" "text/html"}
            :body (format
                   "<html><body><p>login: [<a href=\"%s\">gmail</a></body>][<a href=\"%s\">myopenid</a>]</p></body></html>"
                   (.createLoginURL
                    (aeu/get-user-service) continue nil "gmail.com" (hash-set ""))
                   (.createLoginURL
                    (aeu/get-user-service) continue nil "myopenid.com" (hash-set "")))
            }))

  (GET "/user/:arg" [arg]
    (str (format "<h1>Ohayo %s from migae-test.user-impl servlet path /user/*!</h1>"
                 arg)
         "\n\n<a href='/'>home</a>"))

  (route/not-found "<h1>user service test - page not found</h1>"))

(def user-handler
  (-> #'user-routes
      wrap-params
      wrap-file-info
      ))

