(ns slink.db.redis
  (:require [taoensso.carmine :as redis :refer (wcar)]
            [taoensso.carmine.connections :refer [conn-pool]]
            [slink.config :refer [config]])
  (:import (java.io Closeable)))


(def redis-conn-pool (delay
                       (conn-pool :mem/fresh (->
                                               :redis
                                               config
                                               (select-keys [:min-idle-per-key
                                                             :max-total-per-key])))))

(defn start-conn-pool []
  @redis-conn-pool)

(defn stop-conn-pool []
  (.close ^Closeable @redis-conn-pool))

(defn- get-redis-spec []
  (let [redis-config (config :redis)]
    (if (:uri redis-config)
      (select-keys redis-config [:uri])
      (select-keys redis-config [:host :port :password :db]))))

(def get-redis-config
  {:pool @redis-conn-pool
   :spec (get-redis-spec)})

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
      nil)))

(defn fetch-url-from-redis [hash]
  (try
    (with-redis
      (redis/get hash))
    (catch Exception e
      (println "REDIS ERROR while fetching ->" hash)
      nil)))