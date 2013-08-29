(ns migae.migae-memcache-test
  (:refer-clojure :exclude (contains? get))
  (:import [com.google.appengine.tools.development.testing
            LocalServiceTestHelper
            LocalServiceTestConfig
            LocalMemcacheServiceTestConfig])
  (:use clojure.test
        [migae.migae-memcache :as mc]))


;; (defn- make-local-services-fixture-fn [services hook-helper]
(defn- ds-fixture
  [test-fn]
  (let [helper (LocalServiceTestHelper.
                (into-array LocalServiceTestConfig
                            [(LocalMemcacheServiceTestConfig.)]))]
    (do (.setUp helper)
        (mc/get-memcache-service)
        (test-fn)
        (.tearDown helper))))

;(use-fixtures :once (fn [test-fn] (ds/get-datastore-service) (test-fn)))
(use-fixtures :once ds-fixture)

(deftest ^:init mc-init
  (testing "MC init"
    (is (= com.google.appengine.api.memcache.MemcacheServiceImpl
           (class (mc/get-memcache-service))))
    (is (= com.google.appengine.api.memcache.MemcacheServiceImpl
           (class @*memcache-service*)))))
