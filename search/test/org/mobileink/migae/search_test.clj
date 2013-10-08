(ns org.mobileink.migae.search-test
  (:import [com.google.appengine.tools.development.testing
            LocalServiceTestHelper
            LocalServiceTestConfig
            LocalSearchServiceTestConfig]
           [java.util Date])
  (:require [clojure.test :refer :all]
            [org.mobileink.migae.search :as srch]
            [clojure.tools.logging :as log :only [debug info warn]]))
            ;; [ring-zombie.core :as zombie]))

(defn- search-fixture
  [test-fn]
  (let [helper (LocalServiceTestHelper.
                (into-array LocalServiceTestConfig
                            [(LocalSearchServiceTestConfig.)]))]
    (do (.setUp helper)
        (srch/get-search-service) 
        (test-fn)
        (.tearDown helper))))

;(use-fixtures :once (fn [test-fn] (srch/get-search-service) (test-fn)))
(use-fixtures :each search-fixture)

(deftest ^:init search-init
  (testing "DS init"
    (is (= com.google.appengine.api.search.SearchServiceImpl
           (class (srch/get-search-service))))
    (is (= com.google.appengine.api.search.SearchServiceImpl
           (class @srch/*search-service*)))))

(deftest ^:doc search-doc-1
  (testing "search doc 1"
    (let [d (Date.)
          theDoc (srch/make-doc
                  {:ID "mydoc"
                   :ATOM {:atomfld1 "some test text max 500 chars"
                          :atomfld2 "some more test text max 500 chars"}
                   :DATE {:datefld1 d
                          :datefld2 d}
                   :HTML {:htmlfld1 "<html><p>some test html</p></html>"
                          :htmlfld2 "<html><p>some more test html</p></html>"}
                   :NUMBER {:nbrfld1 1024
                            :nbrfld2 12345}
                   :TEXT {:textfld1 "some test text max 1 MB"
                          :textfld2 "some more test text"}})
          fld1 (.getOnlyField theDoc "atomfld1")]
      (is (= (.getAtom (.getOnlyField theDoc "atomfld1"))
             "some test text max 500 chars"))
      (is (= (.getAtom (.getOnlyField theDoc "atomfld2"))
             "some more test text max 500 chars"))
      (is (= (.getDate (.getOnlyField theDoc "datefld1"))
             d))
      (is (= (.getDate (.getOnlyField theDoc "datefld2"))
             d))
      (is (= (.getHTML (.getOnlyField theDoc "htmlfld1"))
             "<html><p>some test html</p></html>"))
      (is (= (.getHTML (.getOnlyField theDoc "htmlfld2"))
             "<html><p>some more test html</p></html>"))
      (is (= (.getNumber (.getOnlyField theDoc "nbrfld1"))
             (double 1024)))
      (is (= (.getNumber (.getOnlyField theDoc "nbrfld2"))
             (double 12345)))
      (is (= (.getText (.getOnlyField theDoc "textfld1"))
             "some test text max 1 MB"))
      (is (= (.getText (.getOnlyField theDoc "textfld2"))
             "some more test text")))))

(deftest ^:doc search-doc-2
  (testing "search doc 2"
    (let [d (Date.)
          theDoc (srch/make-doc
                  {:ID "mydoc"
                   :ATOM {:atomfld1 "some test text max 500 chars"
                          :atomfld2 "some more test text max 500 chars"
                          :atomfld3 "some 3 more test text max 500 chars"
                          :atomfld4 "some 4 more test text max 500 chars"}
                   :DATE {:datefld1 d
                          :datefld2 d}
                   :HTML {:htmlfld1 "<html><p>some test html</p></html>"
                          :htmlfld2 "<html><p>some more test html</p></html>"}
                   :NUMBER {:nbrfld1 1024
                            :nbrfld2 12345}
                   :TEXT {:textfld1 "some test text max 1 MB"
                          :textfld2 "some more test text"}})]
      (is (= (srch/atom theDoc :atomfld1) "some test text max 500 chars"))
      (is (= (srch/date theDoc :datefld1) d))
      (is (= (srch/html theDoc :htmlfld1) "<html><p>some test html</p></html>"))
      (is (= (srch/nbr  theDoc :nbrfld1) (double 1024)))
      (is (= (srch/text theDoc :textfld1) "some test text max 1 MB")))))

(deftest ^:doc search-doc-3
  (testing "search doc 3"
    (let [d (Date.)
          response (srch/persist
                    :MyDocs
                    {:docid "mydoc"
                     :ATOM {:atomfld1 "some test text max 500 chars"
                            :atomfld2 "some more test text max 500 chars"
                            :atomfld3 "some 3 more test text max 500 chars"
                            :atomfld4 "some 4 more test text max 500 chars"}
                     :DATE {:datefld1 d
                            :datefld2 d}
                     :HTML {:htmlfld1 "<html><p>some test html</p></html>"
                            :htmlfld2 "<html><p>some more test html</p></html>"}
                     :NUMBER {:nbrfld1 1024
                              :nbrfld2 12345}
                     :TEXT {:textfld1 "some test text max 1 MB"
                            :textfld2 "some more test text"}})]
      ;; (is (= (srch/atom theDoc :atomfld1) "some test text max 500 chars"))
      ;; (is (= (srch/date theDoc :datefld1) d))
      ;; (is (= (srch/html theDoc :htmlfld1) "<html><p>some test html</p></html>"))
      ;; (is (= (srch/nbr  theDoc :nbrfld1) (double 1024)))
      ;; (is (= (srch/text theDoc :textfld1) "some test text max 1 MB"))
      (log/debug "response: " (.toString response)))))

(deftest ^:doc search-doc-4
  (testing "search doc 4"
    (let [d (Date.)
          response (srch/persist
                    :MyDocs
                    {:docid "mydoc"
                     :ATOM {:atomfld1 "some test text max 500 chars"
                            :atomfld2 "some more test text max 500 chars"
                            :atomfld3 "some 3 more test text max 500 chars"
                            :atomfld4 "some 4 more test text max 500 chars"}
                     :DATE {:datefld1 d
                            :datefld2 d}
                     :HTML {:htmlfld1 "<html><p>some test html</p></html>"
                            :htmlfld2 "<html><p>some more test html</p></html>"}
                     :NUMBER {:nbrfld1 1024
                              :nbrfld2 12345}
                     :TEXT {:textfld1 "some test text max 1 MB"
                            :textfld2 "some more test text"}})]
      (doseq [r (iterator-seq (.iterator response))]
        (log/debug "result: " (.getCode r) (.getMessage r))
                  ))))

(deftest ^:doc search-doc-5
  (testing "search doc 5"
    (let [d (Date.)
          doc1 (srch/make-doc
                {:ID "mydoc1"
                 :ATOM {:key "foobar1"}
                 :HTML {:htmlfld1 "<html><p>this is doc 1</p></html>"}})
          doc2 (srch/make-doc
                {:ID "mydoc2"
                 :ATOM {:key "foobar2"}
                 :HTML {:htmlfld1 "<html><p>this is doc 2</p></html>"}})
          doc3 (srch/make-doc
                {:ID "mydoc3"
                 :ATOM {:key "foobar3"}
                 :HTML {:htmlfld1 "<html><p>this is doc 3</p></html>"}})
          response (srch/persistn :MyDocs [doc1 doc2 doc3])]
      (doseq [r (iterator-seq (.iterator response))]
        (log/debug "result: " (.getCode r) (.getMessage r))
                  ))))

(deftest ^:doc search-doc-6
  (testing "search doc 6"
    (let [doc1 (srch/make-doc
                {:ID "mydoc1"
                 :ATOM {:key "foobar1"}
                 :HTML {:htmlfld1 "<html><p>here is doc 1</p></html>"}})
          doc2 (srch/make-doc
                {:ID "mydoc2"
                 :ATOM {:key "foobar2"}
                 :HTML {:htmlfld1 "<html><p>this is doc 2</p></html>"}})
          doc3 (srch/make-doc
                {:ID "mydoc3"
                 :ATOM {:key "foobar3"}
                 :HTML {:htmlfld1 "<html><p>here is doc 3</p></html>"}})
          response (srch/persistn :MyDocs [doc1 doc2 doc3])
          doc (srch/get :MyDocs "mydoc2")]
      (is (= (srch/html doc :htmlfld1)
             "<html><p>this is doc 2</p></html>"))
                  )))

(deftest ^:search search-search-7
  (testing "search search 7"
    (let [queryString "here"
          indexKey :MyDocs
          doc1 (srch/make-doc
                {:ID "mydoc1"
                 :ATOM {:key "foobar1"}
                 :HTML {:htmlfld1 "<html><p>this is doc 1</p></html>"}})
          doc2 (srch/make-doc
                {:ID "mydoc2"
                 :ATOM {:key "foobar2"}
                 :HTML {:htmlfld1 "<html><p>that is doc 2</p></html>"}})
          doc3 (srch/make-doc
                {:ID "mydoc3"
                 :ATOM {:key "foobar3"}
                 :HTML {:htmlfld1 "<html><p>this is doc 3</p></html>"}})
          r (srch/persistn :MyDocs [doc1 doc2 doc3])
          response (srch/find :MyDocs "htmlfld1: this")]
      (is (= (.getNumberFound response) 2))
      (is (= (.getNumberReturned response) 2))
      (log/warn "query result: " response)
      (doseq [r (iterator-seq (.iterator response))]
        (log/warn "result doc: " (srch/html r :htmlfld1))))))
      ;; (is (= (srch/html doc :htmlfld1)
      ;;        "<html><p>this is doc 2</p></html>"))


