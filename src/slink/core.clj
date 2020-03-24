(ns slink.core
  (:require [slink.config :refer [config]]
            [slink.api.handler :refer :all]
            [ring.adapter.jetty :as jetty]))

(println (format "Running server at %s:%s" (config :host) (config :port)))

(jetty/run-jetty hello-handler {:port  (config :port)
                                :join? true})