(ns solace-clojure-translations.direct-publish-subscribe.direct_publisher_test
  (:require [clojure.test :refer :all]
            [solace-clojure-translations.direct-publish-subscribe.direct-publisher :refer :all])
  (:import (com.solacesystems.jcsmp JCSMPProperties
                                    JCSMPChannelProperties JCSMPSession InvalidPropertiesException BytesXMLMessage TextMessage XMLMessageConsumer)))


(defn loop-function [arg1] arg1)
(def test-args1 '["1" "2" "3"])

(deftest loop-function-test
  (is (= test-args1 (loop-function test-args1))))
(deftest check-args-test-success (is (= test-args1
         (check-args test-args1 3 loop-function))))

(deftest check-args-not-enough
  (is (= nil
         (check-args test-args1 4 loop-function))))

(deftest check-args-too-many
  (is (= nil
         (check-args test-args1 2 loop-function))))

(deftest generate-client-channel-properties-runs
  (println (generate-client-channel-properties 20 5)))

(deftest generate-client-channel-properties-correct-data
  (let [properties (generate-client-channel-properties 20 5)]
    (is (= 20
           (.getReconnectRetries properties)))
    (is (= 5
           (.getConnectRetriesPerHost properties)))))

(def set-up-jcsmp-args '["host" "vpn" "username" "password"])
(deftest set-up-jcsmp-properties-runs
  (let [[host vpn username password] set-up-jcsmp-args]
    (println (set-up-jcsmp-properties host vpn username password))))

(deftest set-up-jcsmp-properties-sets-values
  (let [[host vpn username password] set-up-jcsmp-args
        properties (set-up-jcsmp-properties host vpn username password)]
    (is (=
          host
          (.getProperty
            properties
            JCSMPProperties/HOST)))
    (is (=
          vpn
          (.getProperty
            properties
            JCSMPProperties/VPN_NAME)))
    (is (=
          username
          (.getProperty
            properties
            JCSMPProperties/USERNAME)))
    (is (=
          password
          (.getProperty
            properties
            JCSMPProperties/PASSWORD)))))

(deftest create-session-runs
  (try
    (let [[host vpn username password] set-up-jcsmp-args]
      (println (.getSessionName (create-session host vpn username password))))
    (catch InvalidPropertiesException e "success")))

(def valid-info ["***REMOVED***" "test" "solace-cloud-client" "***REMOVED***"]) ; fill in actual server data here

(deftest create-session-with-valid-info
  (let [[host vpn username password] valid-info
        info (create-session host vpn username password)]
    info)
  )

(def msg "messsage")

(deftest create-text-message-test
  (is (= msg (.getText (create-text-message msg)))))
(def message-atom (atom "stuff"))
(defn reset-msg-atom [] (reset! message-atom "stuff"))


(deftest create-streaming-publish-event-handler-runs
  (reset-msg-atom)
  (.responseReceived
    (create-streaming-publish-event-handler
      #(reset! message-atom msg)
      #("error"))
    "")
  (= msg @message-atom))

(deftest create-streaming-publish-event-handler-works)

(deftest create-xml-listener-runs
  (println (create-xml-listener #(println "success") #(println "error"))))


(deftest create-xml-listener-test
  (defn on-receive [message]
    (reset!
      message-atom
      (.getText message)))

  (def text-message (create-text-message msg))

  (.onReceive
    (create-xml-listener on-receive #(println "error")) text-message)
  (is (= msg @message-atom)))

(deftest create-xml-message-consumer-runs
  (let [[host vpn username password] valid-info]
    (def curr-session (create-session host vpn username password))
    (def consumer (create-xml-message-consumer curr-session #(println "cool" ) #(println "uncool")))
    (.start consumer)))

(deftest example-usage-subscribe
  (let [[host vpn username password] valid-info
        session (create-session host vpn username password )]
    (defn on-receive [msg] (println "Message dump:" (.dump msg))) 
    (subscribe
      session
      (create-topic "try-me"))
    (.start (create-xml-message-consumer session on-receive  ))
    (Thread/sleep (* 1000 10))))

(example-usage-subscribe)

