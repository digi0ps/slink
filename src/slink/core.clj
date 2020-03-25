(ns slink.core
  (:require [slink.config :refer [config]]
            [slink.api.routes :refer [router]]
            [slink.api.middlewares :refer :all]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer :all]
            [reitit.ring :as ring])
  (:gen-class))


(def default-handler (constantly {:status 404 :body ""}))

(def app-handler
  (->
    router
    (ring/ring-handler default-handler)
    (wrap-defaults site-defaults)))

(defn -main []
  (println (format "Running server at %s:%s" (config :host) (config :port)))
  (jetty/run-jetty app-handler {:port  (config :port)
                                :join? false}))