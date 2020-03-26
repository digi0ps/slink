(ns slink.db.core
  (:require [toucan.db :as db]
            [toucan.models :refer :all]
            [slink.config :refer [config]]))

(defn- get-subname [] (let [{:keys [host port db]} (config :database)]
                        (format "//%s:%s/%s" host port db)))

(def ^:private ^:const db-spec
  {:classname   "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname     (get-subname)
   :user        (config :database :user)
   :password    (config :database :password)})

(db/set-default-db-connection! db-spec)
(db/set-default-automatically-convert-dashes-and-underscores! true)

(defmodel Links :links)