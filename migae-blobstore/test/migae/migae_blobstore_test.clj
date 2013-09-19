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

(deftest dump-ent
  (testing "dump entity"
    (let [newEntity (bs/Entities
                        ^{:kind :Employee, :name "asalieri"}
                        {:flda "A", :fldb "B", :fldc 99})]
          (bs/dump-entity newEntity))))

(deftest ^:entities make-entity
  (testing "make-entity"
    ;; (ds/get-blobstore-service)
    (let [theKey (ds/Keys {:kind :Employee, :name "asalieri"})
          e1 (ds/Entities theKey)
          e2 (ds/Entities ^{:kind :Employee, :name "asalieri"}{})
          e3 (ds/Entities ^{:kind :Employee, :id 123}{})]
      (prn "new entity by key: " e1)
      (prn "new entity by emap with name: " e2)
      (prn "new entity by emap with id: " e3)
      )))

