(ns slink.db.redis
           (:require [taoensso.carmine :as redis :refer (wcar)]
                     [slink.config :refer [config]]))

(def get-redis-config
  {:pool {}
   :spec (-> :redis
             config
             (select-keys [:host :port]))})

(defmacro with-redis [& body]
  `(redis/wcar get-redis-config ~@body))

(defn ping []
  (with-redis
    (redis/ping)))

(defn save-hash-with-url [hash url]
  (try
    (with-redis
      (redis/set hash url))
    ::success
    (catch Exception e
      (println "REDIS ERROR while setting ->" hash url)
      ::failure)))

(defn fetch-url-from-redis [hash]
  (try
    (with-redis
      (redis/get hash))
    (catch Exception e
      (println "REDIS ERROR while fetching ->" hash)
      ::failure)))