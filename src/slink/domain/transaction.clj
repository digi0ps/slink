(ns slink.domain.transaction
  (:require [slink.db.core :as db]
            [slink.db.redis :as redis]
            [taoensso.timbre :as logger]))

(defn get-url-for-hash [hash]
  (if-let [link-from-redis (redis/fetch-url-from-redis hash)]
    (do
      (logger/info "REDIS HIT: " hash link-from-redis)
      link-from-redis)
    (do
      (logger/info "REDIS MISS: " hash)
      (when-let [link-from-db (-> hash
                                  db/fetch-link-by-hash
                                  :url)]
        (logger/info "DB HIT; SETTING MISSING REDIS KEY->" hash link-from-db)
        (redis/save-hash-with-url hash link-from-db)
        link-from-db))))