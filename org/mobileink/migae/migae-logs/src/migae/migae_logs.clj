(ns migae.migae-logs
  (:refer-clojure :exclude (send))
  (:import [com.google.appengine.api.log
            LogService
            LogServiceFactory
            LogQuery
            LogQuery$Builder
            LogQueryResult
            RequestLogs]))

(defonce ^{:dynamic true} *log-service* (atom nil))

(defn get-log-service []
  (when (nil? @*log-service*)
    (reset! *log-service* (LogServiceFactory/getLogService)))
  @*log-service*)

(defn fetch [query]
  (.fetch (get-log-service) query))

