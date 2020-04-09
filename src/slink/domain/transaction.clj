(ns slink.domain.transaction
  (:require [slink.db.core :as db]
            [slink.db.redis :as redis]))

(defn get-url-for-hash [hash]
  (if-let [link-from-redis (redis/fetch-url-from-redis hash)]
    (do
      (println "REDIS HIT: " hash link-from-redis)
      link-from-redis)
    (do
      (println "REDIS MISS: " hash)
      (when-let [link-from-db (-> hash
                                  db/fetch-link-by-hash
                                  :url)]
        (println "DB HIT; SETTING MISSING REDIS KEY->" hash link-from-db)
        (redis/save-hash-with-url hash link-from-db)
        link-from-db))))