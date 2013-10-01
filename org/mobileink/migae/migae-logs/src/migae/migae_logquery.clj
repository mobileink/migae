(ns migae.migae-logquery
  (:refer-clojure :exclude (send))
  (:import [com.google.appengine.api.log
            LogQuery
            LogQuery$Builder
            LogQueryResult]))

(defn withDefaults []
  (LogQuery$Builder/withDefaults))

(defn withIncludeAppLogs [bool]
  (LogQuery$Builder/withIncludeAppLogs bool))

;; query.includeAppLogs(true);
(defn includeAppLogs [qry bool]
  (.includeAppLogs qry bool))

(defn offset [qry n]
  (.offset qry n))

