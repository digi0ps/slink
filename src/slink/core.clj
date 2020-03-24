(ns slink.core
  (:require [slink.config :refer [config]]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(println "CONFIG" (config))