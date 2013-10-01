(ns migae.migae-channel
  (:refer-clojure :exclude [send])
  (:import [com.google.appengine.api.channel
            ChannelServiceFactory
            ChannelService
            ChannelPresence
            ChannelMessage]
           [javax.servlet.http ;; HttpServlet
            HttpServletRequest]
            ;;HttpServletResponse
           ))

(defonce ^{:dynamic true} *channel-service* (atom nil))

(defrecord ClientStatus [id status])

(defn- get-channel-service []
  (when (nil? @*channel-service*)
    (reset! *channel-service* (ChannelServiceFactory/getChannelService)))
  @*channel-service*)

(defn create-channel
  "Creates a channel associated with the provided clientId and returns a token that is valid for the specified period of time (default: 2 hours)."
  [^String client-id & ^int duration]
  (.createChannel (get-channel-service) client-id))

(defn parse-message
  "Parse the incoming message in request.
This method should only be called within a channel webhook."
  [^HttpServletRequest request]
  (.parseMessage (get-channel-service) request))

(defn parse-presence
  "Parse the incoming presence in request. This method should only be called within a channel presence request handler."
  [^HttpServletRequest request]
  (let [presence-obj (.parsePresence (get-channel-service) (:request request))]
    (ClientStatus. (.clientId presence-obj)
                   (if (.isConnected presence-obj) :connected :disconnected))))

Presence:
  String clientId()
  boolean isConnected()

;; (defn make-message [^String client-id, ^String message]
;;   (ChannelMessage. client-id message))

(defn send-message
  ([^ChannelMessage message]
     (.sendMessage (get-channel-service) message))
  ([^String client-id, ^String message]
     (send-message (ChennelMessage. client-id message))))

