(ns migae.migae-xmpp-test
  (:refer-clojure :exclude (contains? get))
  (:import [com.google.appengine.tools.development.testing
            LocalServiceTestHelper
            LocalServiceTestConfig
            LocalXMPPServiceTestConfig])
  (:use clojure.test
        [migae.migae-xmpp :as chan]
        [clojure.tools.logging :as log :only [debug info]]))

(defn- xmpp-fixture
  [test-fn]
  (let [helper (LocalServiceTestHelper.
                (into-array LocalServiceTestConfig
                            [(LocalXMPPServiceTestConfig.)]))]
    (do (.setUp helper)
        (log/debug "debug msg 1")
        (log/debug "debug msg 2")
        (logs/get-log-service)
        (test-fn)
        (.tearDown helper))))

(use-fixtures :each xmpp-fixture)

(deftest ^:init xmpp-init
  (testing "Xmpp init"
    (is (= com.google.appengine.api.xmpp.XMPPServiceImpl
           (class (logs/get-xmpp-service))))
    (is (= com.google.appengine.api.xmpp.XMPPServiceImpl
           (class @*xmpp-service*)))))

(deftest ^:fetch xmpp-fetch-1
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
