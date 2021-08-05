(ns solace-clojure-translations.direct-publish-subscribe.direct_publisher_test
  (:require [clojure.test :refer :all]
            [solace-clojure-translations.direct-publish-subscribe.direct_publisher :refer :all])
  (:import (com.solacesystems.jcsmp JCSMPProperties)))


(defn loop-function [arg1] arg1)
(def test-args1 '["1" "2" "3"])
(defn debugPrint [value] (println value) value)

(deftest loop-function-test
  (is (= test-args1 (loop-function test-args1))))

(deftest check-args-test-success
  (is (= test-args1
         (check-args test-args1 3 loop-function))))

(deftest check-args-not-enough
  (is (= nil
         (check-args test-args1 4 loop-function))))

(deftest check-args-too-many
  (is (= nil
         (check-args test-args1 2 loop-function))))

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

