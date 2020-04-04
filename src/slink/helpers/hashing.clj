(ns slink.helpers.hashing
  (:require [digest :as digest]
            [slink.config :refer [config]]))

(defn print-return [x]
  (println x)
  x)

(defn generate-hash [user-id url]
  (-> (format "%s~%s~%s" url user-id (System/currentTimeMillis))
      (print-return)
      (digest/md5)
      (subs 0 (config :hash :length))))