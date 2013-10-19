(ns org.mobileink.migae.env
  (:import [com.google.apphosting.api ApiProxy]
           [com.google.appengine.api.utils SystemProperty]))

;;(declare migae-type)

;; (defn gae-environment-type []
;;   (let [env-property (System/getProperty "com.google.appengine.runtime.environment")]
;;     (cond
;;      (= env-property "Development") :dev-appserver
;;      (= env-property "Production") :production
;;      (nil? env-property) (try
;;                            (let [stack-trace (.getStackTrace (Thread/currentThread))]
;;                              (if (some #(.contains (.toString %) "clojure.lang.Compiler.compile")
;;                                        stack-trace)
;;                                  :compiling
;;                                  :interactive))
;;                            (catch java.security.AccessControlException ace
;;                              :production)))))

(defn gaeRte [] (System/getProperty "com.google.appengine.runtime.environment"))
(defn sysRte [] (-> (SystemProperty/environment) (.value)))

(defn sysVersion [] (-> (SystemProperty/version) (.get)))

(defn sysAppId [] (-> (SystemProperty/applicationId) (.get)))
(defn gaeAppId [] (-> (ApiProxy/getCurrentEnvironment) .getAppId))

(defn sysAppVersion [] (-> (SystemProperty/applicationVersion) (.get)))
(defn gaeVersionId [] (-> (ApiProxy/getCurrentEnvironment) .getVersionId))

(defn gaeUserLoggedIn? [] (-> (ApiProxy/getCurrentEnvironment) .isLoggedIn))

(defn gaeUserIsAdmin? [] (-> (ApiProxy/getCurrentEnvironment) .isAdmin))

(defn gaeUserEmail [] (-> (ApiProxy/getCurrentEnvironment) .getEmail))

(defn gaeRemainingMillis [] (-> (ApiProxy/getCurrentEnvironment) .getRemainingMillis))

(defn gaeAuthDomain [] (-> (ApiProxy/getCurrentEnvironment) .getAuthDomain))

(defn sysOpSys []
  (let [os-name (.toLowerCase (System/getProperty "os.name"))]
    (cond (.startsWith os-name "mac os x")      :mac
          (.startsWith os-name "windows")       :windows
          (.startsWith os-name "linux")         :linux
          (re-matches #".*bsd.*" os-name)       :bsd
          (or (.startsWith os-name "solaris")
              (.startsWith os-name "sunos")
              (.startsWith os-name "irix")
              (.startsWith os-name "hp-ux")
              (.startsWith os-name "aix")
              (re-matches #".*unix.*" os-name)) :unix
              :else nil)))

;; etc:
    ;; env/fileSeparator
    ;; env/pathSeparator
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
