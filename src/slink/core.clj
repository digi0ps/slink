(ns slink.core
  (:require [slink.config :refer [config]]
            [slink.api.routes :refer [router]]
            [slink.api.middlewares :as mw]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [reitit.ring :as ring])
  (:gen-class))


(def default-handler (constantly {:status 404 :body ""}))

(def app-handler
  (->
    router
    (ring/ring-handler default-handler)
    (wrap-defaults site-defaults)
    (wrap-json-params)
    (wrap-json-response)
    (mw/wrap-content-type-json)))

(defn -main []
  (println (format "Running server at %s:%s" (config :host) (config :port)))
  (jetty/run-jetty app-handler {:port  (config :port)
                                :join? false}))