(ns slink.db.migrations
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [slink.config :refer [config]]
            [taoensso.timbre :as logger]))

(defn get-jdbc-uri []
  (let [{:keys [server-name
                port-number
                database-name
                username
                password]} (config :database)]
    (format "jdbc:postgresql://%s:%s/%s?user=%s&password=%s"
            server-name port-number database-name username password)))

(defn load-config []
  {:datastore  (jdbc/sql-database {:connection-uri (get-jdbc-uri)})
   :migrations (jdbc/load-resources "migrations")
   :reporter   (fn [_ op id]
                 (case op
                   :up (println "Applying migration ->" id)
                   :down (println "Rolling back migration ->" id)))})

(def ragtime-config (delay load-config))

(defn migrate []
  (repl/migrate (load-config))
  (logger/info "Ran all migrations"))

(defn rollback []
  (repl/rollback (load-config)))