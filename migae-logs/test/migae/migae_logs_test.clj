(ns migae.migae-logs-test
  (:refer-clojure :exclude (contains? get))
  (:import [com.google.appengine.tools.development.testing
            LocalServiceTestHelper
            LocalServiceTestConfig
            LocalLogServiceTestConfig]
           [google.appengine.api.memcache.InvalidValueException])
  (:use clojure.test
        [migae.migae-logs :as logs]
        [migae.migae-logquery :as logqry]
        [clojure.tools.logging :as log :only [debug info]]))

(defn- logs-fixture
  [test-fn]
  (let [helper (LocalServiceTestHelper.
                (into-array LocalServiceTestConfig
                            [(LocalLogServiceTestConfig.)]))]
    (do (.setUp helper)
        (log/debug "debug msg 1")
        (log/debug "debug msg 2")
        (logs/get-log-service)
        (test-fn)
        (.tearDown helper))))

(use-fixtures :each logs-fixture)

(deftest ^:init logs-init
  (testing "Logs init"
    (is (= com.google.appengine.api.log.LogServiceImpl
           (class (logs/get-log-service))))
    (is (= com.google.appengine.api.log.LogServiceImpl
           (class @*log-service*)))))

(deftest ^:fetch logs-fetch-1
  (testing "Logs fetch 1"
    (do
        (log/debug "debug msg 1")
        (log/warn "warn msg 2")
        (log/info "info msg 3")
      (let [q (logqry/offset
               (logqry/withIncludeAppLogs true)
               nil)
            logs (logs/fetch q)
            foo (println logs)
            ilogs (.iterator logs)
            bar (println (.hasNext ilogs))]
        (doseq [log (iterator-seq ilogs)]
          (println "log: " log))))))
