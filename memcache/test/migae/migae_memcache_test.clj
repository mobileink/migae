(ns migae.migae-memcache-test
  (:refer-clojure :exclude (contains? get))
  (:import [com.google.appengine.tools.development.testing
            LocalServiceTestHelper
            LocalServiceTestConfig
            LocalMemcacheServiceTestConfig]
           [google.appengine.api.memcache.InvalidValueException])
  (:use clojure.test
        [migae.migae-memcache :as mc]))

(defn- mc-fixture
  [test-fn]
  (let [helper (LocalServiceTestHelper.
                (into-array LocalServiceTestConfig
                            [(LocalMemcacheServiceTestConfig.)]))]
    (do (.setUp helper)
        (mc/get-memcache-service)
        (test-fn)
        (.tearDown helper))))

(use-fixtures :each mc-fixture)

(deftest ^:init mc-init
  (testing "MC init"
    (is (= com.google.appengine.api.memcache.MemcacheServiceImpl
           (class (mc/get-memcache-service))))
    (is (= com.google.appengine.api.memcache.MemcacheServiceImpl
           (class @*memcache-service*)))))

;; api:
;; mc/statistics
;; mc/clear-all!
;; mc/contains?
;; mc/delete!
;; mc/get
;; mc/put!
;; mc/put-map!
;; mc/increment!
;; mc/increment-map!


 ;; assertFalse(ms.contains("yar"));
 ;;    ms.put("yar", "foo");
 ;;    assertTrue(ms.contains("yar"));
(deftest ^:put test-put-1
  (testing "mc put 1"
    (is (= (mc/contains? "yar")
           false))
    (mc/put! "yar" "foo")
    (is (= (mc/contains? "yar")
           true))))
(deftest ^:put test-put-2
  (testing "mc put 2"
    (is (= (mc/contains? "yar") false))
    (mc/put! "yar" "foo")
    (is (= (mc/get "yar") "foo"))
    (mc/put! "yar" "bar")
    (is (= (mc/contains? "yar") true))
    (mc/put! "yar" "bar")
    (is (= (mc/get "yar") "bar"))
    ))
(deftest ^:put test-put-3
  (testing "mc put 3"
    (let [stats (mc/statistics)]
      (is (= (mc/contains? "yar") false))
      (mc/put! "yar" "foo" :policy :replace-only)
      (is (= (mc/contains? "yar") false))
      (is (= (:item-count stats)
             (:item-count (mc/statistics))))
      )))

(deftest ^:stats test-stats-1
  (testing "mc stats"
    ;; (println (mc/statistics))
    (is (= (:item-count (mc/statistics))
           0))
    (mc/put! "key1" "val1")
    (is (= (:item-count (mc/statistics)
           1)))
    ;; (mc/put! "key2" "val2")
    ;; (is (= (:item-count (mc/statistics)
    ;;        2)))
    ))

(deftest ^:stats test-stats-2
  (testing "mc stats 2"
    (is (= (:item-count (mc/statistics))
           0))
    (mc/put! "key1" "val1")
    (mc/put! "key2" "val2")
    (mc/put! "key3" "val3")
    (is (= (:item-count (mc/statistics)
           3)))
    (mc/clear-all!)
    (is (= (:item-count (mc/statistics)
           0)))
    ))
(deftest ^:stats test-stats-3
  (testing "mc stats 3"
    (is (= (mc/contains? "yar") false))
    (mc/put! "yar" "foo" :policy :replace-only)
    (is (= (mc/contains? "yar") false))
    (is (= (:item-count (mc/statistics)) 0))
    (is (= (:miss-count (mc/statistics)) 2))
    ))
(deftest ^:stats test-stats-3a
  (testing "mc stats 3a"
    (is (= (mc/contains? "yar") false))
    (mc/put! "yar" "foo" :policy :replace-only)
    (is (= (mc/contains? "yar") false))
    (mc/put! "yar" "foo" :policy :always)
    (is (= (mc/contains? "yar") true))
    (is (= (:item-count (mc/statistics)) 1))
    (is (= (:hit-count (mc/statistics)) 1))
    (is (= (:miss-count (mc/statistics)) 2))
    ))
(deftest ^:stats test-stats-4
  (testing "mc stats 4"
    (is (= (mc/contains? "yar") false))
    (is (= (:miss-count (mc/statistics)) 1))
    (mc/put! "yar" "foo" :policy :always)
    (is (= (:item-count (mc/statistics)) 1))
    (is (= (mc/contains? "yar") true))
    (is (= (:hit-count (mc/statistics)) 1))
    ))
(deftest ^:stats test-stats-5
  (testing "mc stats 5"
    (is (= (mc/contains? "yar") false))
    (is (= (:item-count (mc/statistics)) 0))
    (is (= (:miss-count (mc/statistics)) 1))
    (is (= (:hit-count (mc/statistics)) 0))

    (mc/put! "yar" "foo" :policy :add-if-not-present)
    (is (= (:item-count (mc/statistics)) 1))
    (is (= (:miss-count (mc/statistics)) 1))
    (is (= (:hit-count (mc/statistics)) 0))

    (is (= (mc/get "yar") "foo"))
    (is (= (:miss-count (mc/statistics)) 1))
    (is (= (:hit-count (mc/statistics)) 1))

    (mc/put! "yar" "bar" :policy :add-if-not-present)
    (is (= (:item-count (mc/statistics)) 1))
    (is (= (:miss-count (mc/statistics)) 1))
    (is (= (:hit-count (mc/statistics)) 1))

    (is (= (mc/get "yar") "foo"))
    (is (= (:item-count (mc/statistics)) 1))
    (is (= (:miss-count (mc/statistics)) 1))
    (is (= (:hit-count (mc/statistics)) 2))

    (is (= (mc/contains? "yar") true))
    (is (= (:item-count (mc/statistics)) 1))
    (is (= (:miss-count (mc/statistics)) 1))
    (is (= (:hit-count (mc/statistics)) 3))
    ))

(deftest ^:incr test-incr-1
  (testing "mc incr 1"
    (is (= (mc/contains? "yar") false))
    (is (= (:item-count (mc/statistics)) 0))
    (is (= (:hit-count (mc/statistics)) 0))
    (is (= (:miss-count (mc/statistics)) 1))

    (mc/increment! "yar" 1)
    (is (= (:item-count (mc/statistics)) 0))
    (is (= (:hit-count (mc/statistics)) 0))
    (is (= (:miss-count (mc/statistics)) 2))

    (mc/increment! "yar" 1 :initial 0)
    (is (= (:item-count (mc/statistics)) 1))
    (is (= (:hit-count (mc/statistics)) 1))
    (is (= (:miss-count (mc/statistics)) 2))

    (is (= (mc/get "yar") 1))
    (is (= (:hit-count (mc/statistics)) 2))

    (is (= (mc/contains? "yar") true))
    (is (= (:hit-count (mc/statistics)) 3))
    ))

(deftest ^:incr test-incr-2
  (testing "mc incr 2"
    (is (= (mc/contains? "yar") false))
    (mc/put! "yar" "foo")
    (try (mc/increment! "yar" 1)
         (catch Exception e
           (is (= "Non-incrementable value for key 'yar'"
                  (.getMessage e)))))
    ))
