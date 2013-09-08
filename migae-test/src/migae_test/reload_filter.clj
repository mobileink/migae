(ns migae-test.reload-filter
  (:import (javax.servlet Filter FilterChain FilterConfig
                          ServletRequest ServletResponse))
  (:require [clojure.tools.logging :as log :only [debug info]])
  (:gen-class :implements [javax.servlet.Filter]))

(defn -init [^Filter this ^FilterConfig cfg])

(defn -doFilter
  [^Filter this
   ^ServletRequest rqst
   ^ServletResponse resp
   ^FilterChain chain]
  (do
    (log/info "reloading...")
    (require
     'migae-test.blobstore-impl
     'migae-test.channel-impl
     'migae-test.datastore-impl
     'migae-test.images-impl
     'migae-test.mail-impl
     'migae-test.memcache-impl
     'migae-test.taskqueues-impl
     'migae-test.url-fetch-impl
     'migae-test.user-impl
     ;; :verbose
     :reload)
    (.doFilter chain rqst resp)))
