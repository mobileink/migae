(ns org.mobileink.migae.env-test
;  (:refer-clojure :exclude (contains? get))
  (:import [com.google.appengine.tools.development.testing
            LocalServiceTestHelper
            LocalServiceTestConfig
            LocalUserServiceTestConfig])
  (:use clojure.test
        [org.mobileink.migae.env :as gae]))
  ;; (:import [com.google.appengine.tools.development.testing
  ;;           LocalServiceTestHelper
  ;;           LocalServiceTestConfig])
  ;; (:use clojure.test
  ;;       [migae.migae-env :as e]))

(defn- ds-fixture
  [test-fn]
  (let [helper (.. (LocalServiceTestHelper.
                    (into-array LocalServiceTestConfig
                                [(LocalUserServiceTestConfig.)]))
                   (setEnvIsAdmin true)
                   (setEnvIsLoggedIn true)
                   (setEnvEmail "dev@example.org")
                   ;; (setEnvVersionId "0.1.0")
                   (setEnvAppId "migae-env-test"))]

  ;; (let [helper (.setEnvIsAdmin (new LocalServiceTestHelper
  ;;                               (into-array LocalServiceTestConfig
  ;;                                           (new LocalUserServiceTestConfig)))
  ;;                              true)]
  ;;                           [(LocalMemcacheServiceTestConfig.)]))]
    (do (.setUp helper)
        (test-fn)
        (.tearDown helper))))

;(use-fixtures :once (fn [test-fn] (ds/get-datastore-service) (test-fn)))
(use-fixtures :once ds-fixture)

(deftest ^:init env-init
  (testing "ENV init"
    (println (format "email: %s" (gae/gaeUserEmail)))
    (println (format "GAE App ID: %s" (gae/gaeAppId)))
    (println (format "Sys App ID: %s" (gae/sysAppId)))
    (println (format "GAE Version ID: %s" (gae/gaeVersionId)))
    (is (= true (gae/gaeUserIsAdmin?)))
    (is (= true (gae/gaeUserLoggedIn?)))
    (is (= "dev@example.org" (gae/gaeUserEmail)))
    (is (= "migae-env-test" (gae/gaeAppId)))
    ))
