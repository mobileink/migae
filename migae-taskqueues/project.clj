(defproject migae/service/migae-taskqueues "0.1.0-SNAPSHOT"
  :description "migae - Mobile Ink Google App Engine sdk for Clojure."
  :url "https://github.com/greynolds/migae"
  :min-lein-version "2.0.0"
  :exclusions [org.clojure/clojure]
  :plugins [[lein-sub "0.2.1"]
            [codox "0.6.4"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [migae/migae-kernel "0.1.0-SNAPSHOT"]])
