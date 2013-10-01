(ns migae.migae-xmpp
  (:refer-clojure :exclude [send])
  (:import [com.google.appengine.api.xmpp
            JID
            Message
            MessageBuilder
            SendResponse
            XMPPService
            XMPPServiceFactory])

(defonce ^{:dynamic true} *xmpp-service* (atom nil))

(defrecord ClientStatus [id status])

(defn- get-xmpp-service []
  (when (nil? @*xmpp-service*)
    (reset! *xmpp-service* (XMPPServiceFactory/getXMPPService)))
  @*xmpp-service*)

;; (defn parse-message
;;   "Parse the incoming message in request.
;; This method should only be called within a xmpp webhook."
;;   [^HttpServletRequest request]
;;   (.parseMessage (get-xmpp-service) request))

;; (defn parse-presence
;;   "Parse the incoming presence in request. This method should only be called within a xmpp presence request handler."
;;   [^HttpServletRequest request]
;;   (let [presence-obj (.parsePresence (get-xmpp-service) (:request request))]
;;     (ClientStatus. (.clientId presence-obj)
;;                    (if (.isConnected presence-obj) :connected :disconnected))))

;; Presence:
;;   String clientId()
;;   boolean isConnected()

;; ;; (defn make-message [^String client-id, ^String message]
;; ;;   (XmppMessage. client-id message))

;; (defn send-message
;;   ([^XmppMessage message]
;;      (.sendMessage (get-xmpp-service) message))
;;   ([^String client-id, ^String message]
;;      (send-message (ChennelMessage. client-id message))))

