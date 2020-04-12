(defproject slink "0.1.0-SNAPSHOT"
  :description "SLINK - The Link Shortener"
  :url ""
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "1.0.0"]
                 ; Config
                 [clonfig "0.2.0"]
                 ; HTTP + Routing
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-devel "1.6.3"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [ring/ring-mock "0.4.0"]
                 [metosin/reitit "0.4.2"]
                 [metosin/reitit-swagger-ui "0.4.2"]
                 ; Migrations + Database
                 [org.postgresql/postgresql "42.2.5"]
                 [ragtime "0.8.0"]
                 [toucan "1.15.1"]
                 [hikari-cp "2.11.0"]
                 ; Redis
                 [com.taoensso/carmine "2.19.1"]
                 ; Hashing
                 [digest "1.4.9"]]
  :main slink.core
  :min-lein-version "2.5.3"
  :uberjar-name "slink.jar"
  :ring {:handler slink.core/app-handler}
  :plugins [[lein-ring "0.12.5"]]
  :profiles {:uberjar {:aot :all}}
  :aliases {"migrate"  ["run" "-m" "slink.db.migrations/migrate"]
            "rollback" ["run" "-m" "slink.db.migrations/rollback"]}
  :repl-options {:init-ns slink.core})
