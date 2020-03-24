(defproject slink "0.1.0-SNAPSHOT"
  :description "SLINK - The Link Shortener"
  :url ""
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clonfig "0.2.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]]
  :main slink.core
  :min-lein-version "2.0.0"
  :uberjar-name "slink.jar"
  :profiles {:uberjar {:aot :all}}
  :repl-options {:init-ns slink.core})
