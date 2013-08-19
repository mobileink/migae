(defproject appengine-magic/service "0.6.0-SNAPSHOT"
  :description "Google App Engine sdk for Clojure."
  :url "https://github.com/gcv/cupboard"
  :min-lein-version "2.0.0"
  :repositories {"releases" "http://appengine-magic-mvn.googlecode.com/svn/releases/"
                 "snapshots" "http://appengine-magic-mvn.googlecode.com/svn/snapshots/"}
  :exclusions [org.clojure/clojure]
  :sub ["services/blobstore" "services/channel"
        "services/datastore" "services/images"
        "services/mail" "services/memcache"
        "services/task_queues" "services/url_fetch"
        "services/user"]
  :jar-name "magic-services.jar"
  :plugins [[lein-sub "0.2.1"]
            [codox "0.6.4"]]
  :dependencies [[appengine-magic/lib "0.6.0-SNAPSHOT"]
                 [org.clojure/clojure "1.5.1"]
;                 [ring/ring-core "1.1.0"]
                 [org.apache.commons/commons-exec "1.1"]
                 ;; App Engine supporting essentials
                 [javax.servlet/servlet-api "2.5"]
                 [commons-io "2.4"]
                 [commons-codec "1.7"]
                 [commons-fileupload "1.2.2"]
                 ;; App Engine administrative interface support
                 [tomcat/jasper-runtime "5.0.28"]
                 [org.apache.geronimo.specs/geronimo-jsp_2.1_spec "1.0.1"]
                 [javax.servlet/jstl "1.1.2"] ; repackaged-appengine-jakarta-jstl-1.1.2.jar
                 [taglibs/standard "1.1.2"] ; repackaged-appengine-jakarta-standard-1.1.2.jar
                 [commons-el "1.0"]
                 [appengine-magic/service/blobstore "0.6.0-SNAPSHOT"]
                 [appengine-magic/service/user "0.6.0-SNAPSHOT"]])
