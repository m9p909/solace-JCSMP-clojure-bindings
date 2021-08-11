(defproject solace-clojure-translations "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.solacesystems/sol-jcsmp "10.12.0"]
                 [philoskim/debux "0.7.9"]
                 [environ "1.2.0"]
                 ]
  :plugins [[lein-environ "1.2.0"]]
  :main ^:skip-aot solace-clojure-translations.core
  :target-path "target/%s"
  :profiles {:uberjar       {:aot      :all
                             :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :test          [:project/test :profiles/test]
             ;; only edit :profiles/* in profiles.clj
             :profiles/test {}
             :project/test  {}})
