(defproject migae/service/migae-images "0.1.0-SNAPSHOT"
  :description "migae - Mobile Ink Google App Engine sdk for Clojure."
  :url "https://github.com/greynolds/migae"
  :min-lein-version "2.0.0"
  :exclusions [org.clojure/clojure]
  :jar-name "migae-images.jar"
  :plugins [[lein-sub "0.2.1"]
            [codox "0.6.4"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [migae/migae-kernel "0.1.0-SNAPSHOT"]])
;; ;                 [ring/ring-core "1.1.0"]
;;                  [org.apache.commons/commons-exec "1.1"]
;;                  ;; App Engine supporting essentials
;;                  [javax.servlet/servlet-api "2.5"]
;;                  [commons-io "2.4"]
;;                  [commons-codec "1.7"]
;;                  [commons-fileupload "1.2.2"]
;;                  ;; App Engine administrative interface support
;;                  [tomcat/jasper-runtime "5.0.28"]
;;                  [org.apache.geronimo.specs/geronimo-jsp_2.1_spec "1.0.1"]
;;                  [javax.servlet/jstl "1.1.2"] ; repackaged-appengine-jakarta-jstl-1.1.2.jar
;;                  [taglibs/standard "1.1.2"] ; repackaged-appengine-jakarta-standard-1.1.2.jar
;;                  [commons-el "1.0"]
;;                  ;; main App Engine libraries
;;                  [com.google.appengine/appengine-api-1.0-sdk "1.8.3"]
;;                  [com.google.appengine/appengine-api-labs "1.8.3"]
;;                  [com.google.appengine/appengine-api-stubs "1.8.3"]
;;                  [com.google.appengine/appengine-testing "1.8.3"]
;;                  [com.google.appengine/appengine-tools-sdk "1.8.3"]])
