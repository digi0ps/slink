(ns slink.core
  (:require [slink.config :refer [config]]
            [slink.api.routes :refer [router]]
            [slink.api.middlewares :as mw]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [reitit.ring :as ring]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.tools.logging :as log]
            [slink.helpers.response :as res]
            [slink.db.redis :as redis]
            [slink.db.core :as db])
  (:gen-class))


(defn print-middleware [handler] (fn [req]
                                   (println "LOG: " req)
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
    (mw/wrap-content-type-json)))

(defn- start-conns []
  (redis/start-conn-pool)
  (db/start-conn-pool))

(defn- stop-conns []
  (redis/stop-conn-pool)
  (db/stop-conn-pool))

(def reloadable-handler (wrap-reload #'app-handler))

(defn -main []
  (let [{:keys [host port threads]} (config)]
    (log/infof "Running server at %s:%s" host port)
    (start-conns)
    (jetty/run-jetty app-handler {:port                 port
                                  :min-threads          threads
                                  :max-threads          threads
                                  :join?                false
                                  :send-server-version? false})
    (stop-conns)))