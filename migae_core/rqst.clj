(ns migae.migae-kernel.rqst
  (:import [com.google.apphosting.api ApiProxy]
           [com.google.appengine.api.utils SystemProperty]))

;;  ApiProxy stuff ;;;;;;;;;;;;;;;;
;;  ApiProxy/getCurrentEnvironment:
;; "Environment is a simple data container that provides additional
;; information about the current request (e.g. who is logged in, are
;; they an administrator, etc.)."
;; https://developers.google.com/appengine/docs/java/javadoc/com/google/apphosting/api/ApiProxy.Environment

;; e.g. (require '[migae.kernel.rqst :as aeRqst])
;;      (aeRqst/appId)
(defn appId []
  (-> (ApiProxy/getCurrentEnvironment) .getAppId))

(defn appVersion []
  (-> (ApiProxy/getCurrentEnvironment) .getVersionId))

(defn userLoggedIn? []
  (-> (ApiProxy/getCurrentEnvironment) .isLoggedIn))

(defn userIsAdmin? []
  (-> (ApiProxy/getCurrentEnvironment) .isAdmin))

(defn userEmail []
  (-> (ApiProxy/getCurrentEnvironment) .getEmail))

(defn remainingMillis []
  (-> (ApiProxy/getCurrentEnvironment) .getRemainingMillis))

(defn authDomain []
  (-> (ApiProxy/getCurrentEnvironment) .getAuthDomain))
