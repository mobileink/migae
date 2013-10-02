(ns migae.migae-channel-test
  (:refer-clojure :exclude (contains? get))
  (:import [com.google.appengine.tools.development.testing
            LocalServiceTestHelper
            LocalServiceTestConfig
            LocalChannelServiceTestConfig])
  (:use clojure.test
        [migae.migae-channel :as chan]
        [clojure.tools.logging :as log :only [debug info]]))

(defn- channel-fixture
  [test-fn]
  (let [helper (LocalServiceTestHelper.
                (into-array LocalServiceTestConfig
                            [(LocalChannelServiceTestConfig.)]))]
    (do (.setUp helper)
        (log/debug "debug msg 1")
        (log/debug "debug msg 2")
        (logs/get-log-service)
        (test-fn)
        (.tearDown helper))))

(use-fixtures :each channel-fixture)

(deftest ^:init channel-init
  (testing "Channel init"
    (is (= com.google.appengine.api.channel.ChannelServiceImpl
           (class (logs/get-channel-service))))
    (is (= com.google.appengine.api.channel.ChannelServiceImpl
           (class @*channel-service*)))))

(deftest ^:fetch channel-fetch-1
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
