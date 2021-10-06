(defproject pinboard "0.1.0-SNAPSHOT"
  :description "Explorations with Pinboard data."
  :url "http://alphajuliet.com/ns/pinboard/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/data.xml "0.0.8"]
                 [cheshire "5.10.0"]
                 [ubergraph "0.8.2"]]
  :main ^:skip-aot pinboard.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
