(defproject slink "0.1.0-SNAPSHOT"
  :description "SLINK - The Link Shortener"
  :url ""
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clonfig "0.2.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-devel "1.6.3"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [metosin/reitit-ring "0.4.2"]]
  :main slink.core
  :min-lein-version "2.5.3"
  :uberjar-name "slink.jar"
  :ring {:handler slink.core/app-handler}
  :plugins [[lein-ring "0.12.5"]]
  :profiles {:uberjar {:aot :all}}
  :repl-options {:init-ns slink.core})
