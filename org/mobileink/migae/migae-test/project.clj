(defproject migae-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :min-lein-version "2.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :url "http://example.com/FIXME"

  :migae {:sdk "/usr/local/java/appengine"
          :id "migae-test"
          ;; GAE version ID
          ;; using '-' prefix on version nbr forces user to customize
          :version  {:dev "0-1-0"
                     :test "0-1-1"
                     :prod "1-0-0"}
          :filters [{:filter "reload_filter"
                     :ns "migae-test.reload-filter"
                     :class "migae_test.reload_filter"}]
          :servlets [{:servlet "blobstore",
                      :src "migae_test/blobstore_servlet.clj"
                      :ns "migae-test.blobstore-servlet",
                      :class "migae_test.blobstore_servlet",
                      :filters [{:filter "reload_filter"}],
                      :services [{:service "blobstore" :url-pattern  "/blobstore/*"}]},
                     {:servlet "channel",
                      :src "migae_test/channel_servlet.clj"
                      :ns "migae-test.channel-servlet",
                      :class "migae_test.channel_servlet",
                      :filters [{:filter "reload_filter"}],
                      :services [{:service "channel" :url-pattern  "/channel/*"}]},
                     {:servlet "datastore",
                      :src "migae_test/datastore_servlet.clj"
                      :ns "migae-test.datastore-servlet",
                      :class "migae_test.datastore_servlet",
                      :filters [{:filter "reload_filter"}],
                      :services [{:service "datastore" :url-pattern  "/datastore/*"}]},
                     {:servlet "images",
                      :src "migae_test/images_servlet.clj"
                      :ns "migae-test.images-servlet",
                      :class "migae_test.images_servlet",
                      :filters [{:filter "reload_filter"}],
                      :services [{:service "images" :url-pattern  "/images/*"}]},
                     {:servlet "mail",
                      :src "migae_test/mail_servlet.clj"
                      :ns "migae-test.mail-servlet",
                      :class "migae_test.mail_servlet",
                      :filters [{:filter "reload_filter"}],
                      :services [{:service "mail" :url-pattern  "/mail/*"}]},
                     {:servlet "memcache",
                      :src "migae_test/memcache_servlet.clj"
                      :ns "migae-test.memcache-servlet",
                      :class "migae_test.memcache_servlet",
                      :filters [{:filter "reload_filter"}],
                      :services [{:service "memcache" :url-pattern  "/memcache/*"}]},
                     {:servlet "taskqueues",
                      :src "migae_test/taskqueues_servlet.clj"
                      :ns "migae-test.taskqueues-servlet",
                      :class "migae_test.taskqueues_servlet",
                      :filters [{:filter "reload_filter"}],
                      :services [{:service "taskqueues" :url-pattern  "/taskqueues/*"}]},
                      {:servlet "url-fetch",
                      :src "migae_test/url_fetch_servlet.clj"
                      :ns "migae-test.url-fetch-servlet",
                      :class "migae_test.url_fetch_servlet",
                      :filters [{:filter "reload_filter"}],
                      :services [{:service "url-fetch" :url-pattern  "/urlfetch/*"}]},
                     {:servlet "user",
                      :src "migae_test/user_servlet.clj"
                      :ns "migae-test.user-servlet",
                      :class "migae_test.user_servlet",
                      :filters [{:filter "reload_filter"}],
                      :services [{:service "user" :url-pattern  "/user/*"}
                                 {:service "login" :url-pattern  "/_ah/*"}]}
                     ]
          :war "war"
          :display-name "migae-test"
          :welcome "index.html"
          :threads true,
          :sessions true,
          :java-logging "logging.properties",
          ;; static-files: html, css, js, etc.
          :statics {:src "src/main/public"
                    :dest ""
                    :include {:pattern "public/**"
                              ;; :expire "5d"
                              }
                    ;; :exclude {:pattern "foo/**"}
                    }
          ;; resources: img, etc. - use lein default
          :resources {:src "src/main/resource"
                      :dest ""
                      :include {:pattern "public/**"
                                ;; :expire "5d"
                                }
                      ;; :exclude {:pattern "bar/**"}
                      }
          }
  :aot [#"migae-test.*-servlet"
        #"migae-test.*-filter"]
        ;; migae-test.url-fetch-servlet
        ;; migae-test.user-servlet
        ;; migae-test.reload-filter ]
  :resource-paths ["src/"]
  :web-inf "war/WEB-INF"
  :compile-path "war/WEB-INF/classes"
  :target-path "war/WEB-INF/lib"
  :libdir-path "war/WEB-INF/lib"
  :jar-exclusions [#".*impl*" #"^WEB-INF/appengine-generated.*$"]
  :clean-targets [:web-inf]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring/ring-servlet "1.2.0"]
                 [hiccup "1.0.4"]
                 ;; [migae/migae-env "0.1.0-SNAPSHOT"]
                 [migae/migae-blobstore "0.1.0-SNAPSHOT"]
                 [migae/migae-channel "0.1.0-SNAPSHOT"]
                 [migae/migae-datastore "0.1.0-SNAPSHOT"]
                 [migae/migae-images "0.1.0-SNAPSHOT"]
                 [migae/migae-mail "0.1.0-SNAPSHOT"]
                 [migae/migae-memcache "0.1.0-SNAPSHOT"]
                 [migae/migae-taskqueues "0.1.0-SNAPSHOT"]
                 [migae/migae-urlfetch "0.1.0-SNAPSHOT"]
                 [migae/migae-user "0.1.0-SNAPSHOT"]
                 [org.clojure/tools.logging "0.2.3"]]
  :profiles {:dev {:plugins [[lein-migae "0.1.6-SNAPSHOT"]
                             [lein-libdir "0.1.1"]]}})
