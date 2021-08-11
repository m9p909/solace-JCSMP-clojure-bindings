(ns solace-clojure-translations.direct-publish-subscribe.direct-publisher
  (:import (com.solacesystems.jcsmp JCSMPChannelProperties BytesXMLMessage JCSMPSession JCSMPFactory TextMessage XMLMessageListener Topic JCSMPStreamingPublishCorrelatingEventHandler)
           (com.solacesystems.jcsmp.impl JCSMPBasicSession)))
(import [com.solacesystems.jcsmp JCSMPProperties])
(def cli-args *command-line-args*)

(defn debugPrint [value] (println "debug" value) value)

(defn check-args
  [args num-args function]
  (if
    (= (count args) num-args)
    (do (println args) (function args))
    (println "expected" num-args "args")))

(defn generate-client-channel-properties
  [reconnect-retries connect-retries-per-host]
  (let [channel-props (new JCSMPChannelProperties)]
    (doto channel-props
      (.setReconnectRetries reconnect-retries)
      (.setConnectRetriesPerHost connect-retries-per-host))))

(defn set-up-jcsmp-properties
  ([host
    vpn-name
    username
    password
    reconnect-retries
    connect-retries-per-host
    generate-sequence-numbers]
   (let [properties (new JCSMPProperties)]
     (doto properties
       (.setProperty JCSMPProperties/HOST host)
       (.setProperty JCSMPProperties/VPN_NAME vpn-name)
       (.setProperty JCSMPProperties/USERNAME username)
       (.setProperty JCSMPProperties/PASSWORD password)
       (.setProperty JCSMPProperties/GENERATE_SEQUENCE_NUMBERS
                     generate-sequence-numbers)
       (.setProperty JCSMPProperties/CLIENT_CHANNEL_PROPERTIES
                     (generate-client-channel-properties reconnect-retries connect-retries-per-host)))))
  ([host vpn-name username password]
   ; solace recommended values
   (set-up-jcsmp-properties host vpn-name username password 20 5 true)))

(defn create-session [host vpn-name username password]
    (def session 
      (.createSession
      (JCSMPFactory/onlyInstance)
      (set-up-jcsmp-properties host vpn-name username password)))
    (.connect session)
    session)

(defn create-xml-listener [on-receive on-exception]
  (reify XMLMessageListener
    (onReceive [this msg]
      (on-receive msg))
    (onException [this e] (on-exception e))))

(defn create-xml-message-consumer
  ([session on-receive on-exception]
   (.getMessageConsumer session
                        (create-xml-listener on-receive on-exception)))
  ([session on-receive]
   (create-xml-message-consumer session on-receive (fn [e] (throw e)))))

(defn create-streaming-publish-event-handler [ on-received-ex handle-error-ex]
  (reify JCSMPStreamingPublishCorrelatingEventHandler
    (responseReceivedEx [this object] (on-received-ex object))
    (handleErrorEx [this object exception long] (handle-error-ex object exception long))))

(defn create-xml-message-producer [])

(defn create-topic [topic-path]
  (.createTopic (JCSMPFactory/onlyInstance) topic-path))

(defn create-text-message [msg]
  (def message
    (.createMessage (JCSMPFactory/onlyInstance) TextMessage))
  (.setText message msg)
  message)


(defn subscribe [session topic]
  ; tested?
  (assert (instance? Topic topic))
  (.addSubscription session topic))

(defn -main
  [args]
  (check-args args 3
              (let [[host vpn username password] args] (set-up-jcsmp-properties
                  host
                  vpn
                  username
                  password))))
