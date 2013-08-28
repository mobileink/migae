(defproject migae/migae-datastore "0.1.0-SNAPSHOT"
  :description "migae - Mobile Ink Google App Engine sdk for Clojure."
  :url "https://github.com/greynolds/migae"
  :min-lein-version "2.0.0"
  :aot [#"datastore.*"]
  :test-selectors {:fields :fields
                   :meta :meta
                   :entities :entities}
  :dependencies [[migae/migae-core "0.1.0-SNAPSHOT"]
                 [org.clojure/clojure "1.5.1"]
                 [com.google.appengine/appengine-api-1.0-sdk "1.8.3"]]
  :profiles {:1.5 {:dependencies [[org.clojure/clojure "1.5.1"]
                                  [com.google.appengine/appengine-api-1.0-sdk "1.8.3"]]}})
