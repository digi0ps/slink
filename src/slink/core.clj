(ns slink.core
  (:require [slink.config :refer [config]]
            [slink.api.handler :refer :all]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(println (format "Running server at %s:%s" (config :host) (config :port)))

(defn -main []
  (jetty/run-jetty hello-handler {:port  (config :port)
                                :join? false}))