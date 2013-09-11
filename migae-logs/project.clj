(defproject migae/migae-logs "0.1.0-SNAPSHOT"
  :description "migae - Mobile Ink Google App Engine sdk for Clojure."
  :url "https://github.com/greynolds/migae"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.google.appengine/appengine-api-1.0-sdk "1.8.3"]]
  :profiles {:test
             {:dependencies [[org.clojure/tools.logging "0.2.3"]
                             [com.google.appengine/appengine-api-1.0-sdk "1.8.3"]
                             [com.google.appengine/appengine-api-stubs "1.8.3"]
                             [com.google.appengine/appengine-testing "1.8.3"]]}})
