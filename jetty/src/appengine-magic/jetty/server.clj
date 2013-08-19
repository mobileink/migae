(ns 'appengine-magic.server)

(use 'appengine-magic.jetty.local-env-helpers
     '[appengine-magic.jetty.servlet :only [servlet]]
;     '[appengine-magic.jetty.swank :only [wrap-swank]]
     '[ring.middleware.file :only [wrap-file]]
     '[ring.middleware.file-info :only [wrap-file-info]])

(require '[clojure.string :as str]
         '[appengine-magic.jetty.jetty :as jetty]
         '[appengine-magic.jetty.blobstore-upload :as blobstore-upload])

(import java.io.File
        com.google.apphosting.api.ApiProxy)

(defonce ^{:dynamic true} *server* (atom nil))


(defn make-appengine-request-environment-filter []
  (reify javax.servlet.Filter
    (init [_ filter-config]
      (.setAttribute (.getServletContext filter-config)
                     "com.google.appengine.devappserver.ApiProxyLocal"
                     (ApiProxy/getDelegate)))
    (destroy [_])
    (doFilter [_ req resp chain]
      (let [all-cookies (.getCookies req)
            login-cookie (when all-cookies
                           (let [raw (first (filter #(= "dev_appserver_login" (.getName %))
                                                    (.getCookies req)))]
                             (when raw (.getValue raw))))
            [user-email user-admin? _] (when login-cookie
                                         (str/split login-cookie #":"))
            thread-environment-proxy (make-thread-environment-proxy :user-email user-email
                                                                    :user-admin? user-admin?)]
        (ApiProxy/setEnvironmentForCurrentThread thread-environment-proxy))
      (.doFilter chain req resp))))

;; ae/start

;; this is to be called from the repl, so we can call it from a lein
;; task, which has access to the project map

;;(defn start [servlet & {:keys [port join? high-replication in-memory]
(defn start [project servlet]
  ;; & {:keys [port join? high-replication in-memory]
  ;;                  :or {port 8080, join? false,
  ;;                       high-replication false, in-memory false}}]
  (let [war-root (java.io.File. (:war-root servlet))
        ;; setup HttpServlet and its service method
        ;; handler-servlet (servlet (:handler servlet))]
        handler-servlet (make-servlet servlet)]
    (appengine-init war-root port high-replication in-memory)
    (reset!
     *server*
     (jetty/start
      {"/*" [(make-appengine-request-environment-filter)
             (com.google.apphosting.utils.servlet.TransactionCleanupFilter.)
             (com.google.appengine.api.blobstore.dev.ServeBlobFilter.)]}
      {"/" handler-servlet
       ;; These mappings are from webdefault.xml in appengine-local-runtime-*.jar.
       "/_ah/admin" (com.google.apphosting.utils.servlet.DatastoreViewerServlet.)
       "/_ah/admin/backends" (com.google.apphosting.utils.servlet.ServersServlet.)
       "/_ah/admin/capabilitiesstatus" (com.google.apphosting.utils.servlet.CapabilitiesStatusServlet.)
       "/_ah/admin/datastore" (com.google.apphosting.utils.servlet.DatastoreViewerServlet.)
       "/_ah/admin/inboundmail" (com.google.apphosting.utils.servlet.InboundMailServlet.)
       "/_ah/admin/search" (com.google.apphosting.utils.servlet.SearchServlet.)
       "/_ah/admin/taskqueue" (com.google.apphosting.utils.servlet.TaskQueueViewerServlet.)
       "/_ah/admin/xmpp" (com.google.apphosting.utils.servlet.XmppServlet.)
       "/_ah/adminConsole" (org.apache.jsp.ah.adminConsole_jsp.)
       "/_ah/backendsBody" (org.apache.jsp.ah.backendsBody_jsp.)
       "/_ah/backendsFinal" (org.apache.jsp.ah.backendsFinal_jsp.)
       "/_ah/backendsHead" (org.apache.jsp.ah.backendsHead_jsp.)
       "/_ah/blobImage" (com.google.appengine.api.images.dev.LocalBlobImageServlet.)
       "/_ah/blobUpload" (com.google.appengine.api.blobstore.dev.UploadBlobServlet.)
       "/_ah/capabilitiesStatusBody" (org.apache.jsp.ah.capabilitiesStatusBody_jsp.)
       "/_ah/capabilitiesStatusFinal" (org.apache.jsp.ah.capabilitiesStatusFinal_jsp.)
       "/_ah/capabilitiesStatusHead" (org.apache.jsp.ah.capabilitiesStatusHead_jsp.)
       "/_ah/capabilitiesViewer" (com.google.apphosting.utils.servlet.CapabilitiesStatusServlet.)
       "/_ah/channel/jsapi" (com.google.appengine.api.channel.dev.ServeScriptServlet.)
       "/_ah/channelLocalChannel" (com.google.appengine.api.channel.dev.LocalChannelServlet.)
       "/_ah/datastoreViewer" (com.google.apphosting.utils.servlet.DatastoreViewerServlet.)
       "/_ah/datastoreViewerBody" (org.apache.jsp.ah.datastoreViewerBody_jsp.)
       "/_ah/datastoreViewerFinal" (org.apache.jsp.ah.datastoreViewerFinal_jsp.)
       "/_ah/datastoreViewerHead" (org.apache.jsp.ah.datastoreViewerHead_jsp.)
       "/_ah/entityDetailsBody" (org.apache.jsp.ah.entityDetailsBody_jsp.)
       "/_ah/entityDetailsFinal" (org.apache.jsp.ah.entityDetailsFinal_jsp.)
       "/_ah/entityDetailsHead" (org.apache.jsp.ah.entityDetailsHead_jsp.)
       "/_ah/inboundmailBody" (org.apache.jsp.ah.inboundMailBody_jsp.)
       "/_ah/inboundmailFinal" (org.apache.jsp.ah.inboundMailFinal_jsp.)
       "/_ah/inboundmailHead" (org.apache.jsp.ah.inboundMailHead_jsp.)
       "/_ah/indexDetailsBody" (org.apache.jsp.ah.indexDetailsBody_jsp.)
       "/_ah/indexDetailsFinal" (org.apache.jsp.ah.indexDetailsFinal_jsp.)
       "/_ah/indexDetailsHead" (org.apache.jsp.ah.indexDetailsHead_jsp.)
       "/_ah/login" (com.google.appengine.api.users.dev.LocalLoginServlet.)
       "/_ah/logout" (com.google.appengine.api.users.dev.LocalLogoutServlet.)
       "/_ah/oauthAuthorizeToken" (com.google.appengine.api.users.dev.LocalOAuthAuthorizeTokenServlet.)
       "/_ah/oauthGetAccessToken" (com.google.appengine.api.users.dev.LocalOAuthAccessTokenServlet.)
       "/_ah/oauthGetRequestToken" (com.google.appengine.api.users.dev.LocalOAuthRequestTokenServlet.)
       "/_ah/queue_deferred" (com.google.apphosting.utils.servlet.DeferredTaskServlet.)
       "/_ah/resources" (com.google.apphosting.utils.servlet.AdminConsoleResourceServlet.)
       "/_ah/searchDocumentBody" (org.apache.jsp.ah.searchDocumentBody_jsp.)
       "/_ah/searchDocumentFinal" (org.apache.jsp.ah.searchDocumentFinal_jsp.)
       "/_ah/searchDocumentHead" (org.apache.jsp.ah.searchDocumentHead_jsp.)
       "/_ah/searchIndexBody" (org.apache.jsp.ah.searchIndexBody_jsp.)
       "/_ah/searchIndexFinal" (org.apache.jsp.ah.searchIndexFinal_jsp.)
       "/_ah/searchIndexHead" (org.apache.jsp.ah.searchIndexHead_jsp.)
       "/_ah/searchIndexesListBody" (org.apache.jsp.ah.searchIndexesListBody_jsp.)
       "/_ah/searchIndexesListFinal" (org.apache.jsp.ah.searchIndexesListFinal_jsp.)
       "/_ah/searchIndexesListHead" (org.apache.jsp.ah.searchIndexesListHead_jsp.)
       "/_ah/sessioncleanup" (com.google.apphosting.utils.servlet.SessionCleanupServlet.)
       "/_ah/taskqueueViewerBody" (org.apache.jsp.ah.taskqueueViewerBody_jsp.)
       "/_ah/taskqueueViewerFinal" (org.apache.jsp.ah.taskqueueViewerFinal_jsp.)
       "/_ah/taskqueueViewerHead" (org.apache.jsp.ah.taskqueueViewerHead_jsp.)
       "/_ah/upload/*" (servlet (blobstore-upload/make-blob-upload-handler war-root))
       "/_ah/xmppBody" (org.apache.jsp.ah.xmppBody_jsp.)
       "/_ah/xmppFinal" (org.apache.jsp.ah.xmppFinal_jsp.)
       "/_ah/xmppHead" (org.apache.jsp.ah.xmppHead_jsp.)}
      :port port
      :join? join?))))

;; ae/stop
(defn stop []
  (when-not (nil? @*server*)
    (appengine-clear)
    (jetty/stop @*server*)
    (reset! *server* nil)))

;; ae/serve
(defn serve [servlet & {:keys [port high-replication in-memory]
                              :or {port 8080, high-replication false, in-memory false}}]
  (stop)
  (start servlet :port port :high-replication high-replication :in-memory in-memory))
