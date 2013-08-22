(ns migae.migae-kernel
  (:import [com.google.apphosting.api ApiProxy]
           [com.google.appengine.api.utils SystemProperty]))

(declare migae-type)

(defn migae-environment-type []
  (let [env-property (System/getProperty "com.google.appengine.runtime.environment")]
    (cond
     (= env-property "Development") :dev-appserver
     (= env-property "Production") :production
     (nil? env-property) (try
                           (let [stack-trace (.getStackTrace (Thread/currentThread))]
                             (if (some #(.contains (.toString %) "clojure.lang.Compiler.compile")
                                       stack-trace)
                                 :compiling
                                 :interactive))
                           (catch java.security.AccessControlException ace
                             :production)))))

(defn rteType
  []
  (-> (SystemProperty/environment) (.value)))

(defn rteVersion
  []
  (-> (SystemProperty/version) (.get)))

(defn appId
  []
  (-> (SystemProperty/applicationId) (.get)))
;; or:
;; (-> (ApiProxy/getCurrentEnvironment) .getAppId)

(defn appVersion
  []
  (-> (SystemProperty/applicationVersion) (.get)))

(defn remainingMillis
  []
  (-> (ApiProxy/getCurrentEnvironment) (.getRemainingMillis)))

;; etc:
    ;; kernel/fileSeparator
    ;; kernel/pathSeparator
    ;; etc.
    ;;     line.separator
    ;;     java.version
    ;;     java.vendor
    ;;     java.vendor.url
    ;;     java.class.version
    ;;     java.specification.version
    ;;     java.specification.vendor
    ;;     java.specification.name
    ;;     java.vm.vendor
    ;;     java.vm.name
    ;;     java.vm.specification.version
    ;;     java.vm.specification.vendor
    ;;     java.vm.specification.name
    ;;     user.dir
