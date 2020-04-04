(ns slink.core
  (:require [slink.config :refer [config]]
            [slink.api.routes :refer [router]]
            [slink.api.middlewares :as mw]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [reitit.ring :as ring]
            [clojure.tools.logging :as log]
            [slink.helpers.response :as res])
  (:gen-class))


(defn print-middleware [handler] (fn [req]
                                   (println "PRINT: " req)
                                   (handler req)))
(def app-handler
  (->
    router
    (ring/ring-handler res/not-found)
    (mw/wrap-exceptions)
    (print-middleware)
    (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
    (wrap-json-params)
    (wrap-json-response)
    (mw/wrap-content-type-json)
    (print-middleware)))

(defn -main []
  (println (format "Running server at %s:%s" (config :host) (config :port)))
  (jetty/run-jetty app-handler {:port  (config :port)
                                :join? false}))