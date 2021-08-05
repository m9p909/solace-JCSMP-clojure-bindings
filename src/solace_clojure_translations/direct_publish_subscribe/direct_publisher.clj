(ns solace-clojure-translations.direct-publish-subscribe.direct_publisher
  )
(import [com.solacesystems.jcsmp JCSMPProperties])
(def cli-args *command-line-args*)

(defn check-args
  [args num-args function]
  (if
    (= (count args) num-args)
    (do (println args) (function args))
    (println "expected" num-args "args")))

(defn set-up-jcsmp-properties [host vpn-name username password]
  (let [properties (new JCSMPProperties)]
    (doto properties
      (.setProperty JCSMPProperties/HOST host)
      (.setProperty JCSMPProperties/VPN_NAME vpn-name)
      (.setProperty JCSMPProperties/USERNAME username)
      (.setProperty JCSMPProperties/PASSWORD password))
    properties)
  )



(defn -main
  [args]
  (check-args args 3
              ()))