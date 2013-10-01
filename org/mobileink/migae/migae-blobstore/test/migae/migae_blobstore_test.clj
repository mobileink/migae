(ns migae.migae-blobstore-test
  (:refer-clojure :exclude [name hash])
  (:import [com.google.appengine.tools.development.testing
            LocalServiceTestHelper
            LocalServiceTestConfig
            LocalMemcacheServiceTestConfig
            LocalMemcacheServiceTestConfig$SizeUnit
            LocalMailServiceTestConfig
            LocalBlobstoreServiceTestConfig
            LocalUserServiceTestConfig])
  (:require [clojure.test :refer :all]
            [migae.migae-blobstore :as bs]))

(defn- bs-fixture
  [test-fn]
  (let [;; environment (ApiProxy/getCurrentEnvironment)
        ;; delegate (ApiProxy/getDelegate)
        helper (LocalServiceTestHelper.
                (into-array LocalServiceTestConfig
                            [(LocalBlobstoreServiceTestConfig.)]))]
    (do (.setUp helper)
        (bs/get-blobstore-service)
        (test-fn)
        (.tearDown helper))))
        ;; (ApiProxy/setEnvironmentForCurrentThread environment)
        ;; (ApiProxy/setDelegate delegate))))

;(use-fixtures :once (fn [test-fn] (bs/get-blobstore-service) (test-fn)))
(use-fixtures :once bs-fixture)

(deftest ^:init bs-init
  (testing "BS init"
    (is (= com.google.appengine.api.blobstore.BlobstoreServiceImpl
           (class (bs/get-blobstore-service))))
    (is (= com.google.appengine.api.blobstore.BlobstoreServiceImpl
           (class @bs/*blobstore-service*)))))

