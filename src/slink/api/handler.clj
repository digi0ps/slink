(ns slink.api.handler
  (:require [slink.db.core :as db]
            [slink.helpers.response :as res]
            [clj-time.coerce :as c]))

(defn hello-handler [request]
  {:status 200
   :body {:message "server is running..."}})


(defn- transform-link [link]
  (println link)
  (let [epoch (c/to-epoch (:created-at link))]
    (assoc link :created-at epoch)))

(defn user-links-handler [request]
  (if-let [user-id (get-in request [:params :user-id])]
    (let [all-links (db/fetch-all-links-for-user user-id)
          epoch-links (map transform-link all-links)]
      (res/success epoch-links))
    (res/error 404 "User ID parameter is required.")))