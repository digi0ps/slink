(ns slink.db.core
  (:require [toucan.db :as db]
            [toucan.models :as models]
            [hikari-cp.core :as hikari]
            [slink.config :refer [config]]
            [clj-time.coerce :as c]))

(def conn-pool (delay
                 (hikari/make-datasource (config :database))))

(defn start-conn-pool []
  (db/set-default-db-connection! {:datasource @conn-pool})
  (db/set-default-automatically-convert-dashes-and-underscores! true)
  (models/set-root-namespace! 'slink.db.models))

(defn stop-conn-pool []
  (hikari/close-datasource @conn-pool))

(defn insert-link [hash url user-id]
  (db/insert! 'Links {:hash    hash
                      :url     url
                      :user-id user-id}))

(defn fetch-all-links-for-user [user-id]
  (db/select 'Links :user-id user-id))

(defn fetch-link-by-hash [hash]
  (db/select-one 'Links :hash hash))

(defn clear-all! []
  (db/simple-delete! 'Links))