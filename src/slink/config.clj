(ns slink.config
  (:require [clonfig.core :as clonfig]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def ^:private ^:const config-file-name "config.edn")

(def config-file (-> config-file-name
                     (io/resource)
                     (slurp)
                     (edn/read-string)))

(defn get-config []
  (println "reading from file")
  (clonfig/read-config config-file))

(def config-state (delay (get-config)))

(defn config
  ([] @config-state)
  ([& keys] (get-in @config-state keys)))