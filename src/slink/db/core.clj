(ns slink.db.core
  (:require [toucan.db :as db]
            [toucan.models :as models]
            [slink.config :refer [config]]
            [clj-time.coerce :as c]))

(defn- get-subname [] (let [{:keys [host port db]} (config :database)]
                        (format "//%s:%s/%s" host port db)))

(def ^:private ^:const db-spec
  {:classname   "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname     (get-subname)
   :user        (config :database :user)
   :password    (config :database :password)})

(println "DB SPEC: " db-spec)

(def setup-db (delay (do
                       (println "Configuring database")
                       (db/set-default-db-connection! db-spec)
                       (db/set-default-automatically-convert-dashes-and-underscores! true)
                       (models/set-root-namespace! 'slink.db.models))))
@setup-db

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