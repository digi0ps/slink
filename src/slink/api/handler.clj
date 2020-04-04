(ns slink.api.handler
  (:require [slink.db.core :as db]
            [slink.helpers.response :as res]
            [ring.util.response :refer [redirect]]
            [clj-time.coerce :as c]
            [slink.config :refer [config]]
            [slink.helpers.regex :refer :all]
            [slink.helpers.hashing :refer :all]))

(defn hello-handler [request]
  {:status 200
   :body {:message "server is running..."}})


(defn- transform-link [link]
  (println link)
  (let [epoch (c/to-epoch (:created-at link))]
    (assoc link :created-at epoch)))

(defn user-links-handler [request]
  (println request)
  (if-let [user-id-query (get-in request [:params :user])]
    (try
      (let [user-id (Integer/parseInt user-id-query)
            all-links (db/fetch-all-links-for-user user-id)
            epoch-links (map transform-link all-links)]
        (res/success epoch-links))
      (catch NumberFormatException e
        (throw (Exception. "User parameter must be an integer."))))
    (res/error 404 "User parameter is required.")))

(defn- generate-slink [{:keys [scheme headers]} hash]
  (format "%s://%s/%s"
          (name scheme)
          (headers "host")
          hash))

(defn create-link-handler [{:keys [params] :as request}]
  (let [{:keys [user url]} params]
    (cond
      (nil? user) (res/error 404 "User parameter is required.")
      (nil? url) (res/error 404 "URL parameter is required.")
      (not (is-valid-url? url)) (res/error 404 "URL is not valid.")
      :else (do
              (let [url-hash (generate-hash user url)
                    slink (generate-slink request url-hash)]
                (db/insert-link url-hash url user)
                (res/success {:slink slink}))))))


(defn redirect-link-handler [request]
  (let [hash (get-in request [:path-params :hash])]
    (println "HERE" hash)
    (if-let [link (db/fetch-link-by-hash hash)]
      (redirect (:url link))
      (res/not-found))))