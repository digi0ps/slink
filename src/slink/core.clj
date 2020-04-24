(ns slink.core
  (:require [slink.config :refer [config]]
            [slink.api.routes :refer [router]]
            [slink.api.middlewares :as mw]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [reitit.ring :as ring]
            [ring.middleware.reload :refer [wrap-reload]]
            [taoensso.timbre :as logger]
            [slink.helpers.response :as res]
            [slink.db.redis :as redis]
            [slink.db.core :as db])
  (:gen-class))


(defn print-middleware [handler] (fn [req]
                                   (logger/debug "REQUEST: " req)
                                   (handler req)))
(def app-handler
  (->
    router
    (ring/ring-handler res/not-found)
    (mw/wrap-cors)
    (mw/wrap-exceptions)
    (print-middleware)
    (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
    (wrap-json-params)
    (wrap-json-response)
    (mw/wrap-content-type-json)))

(defn- start-conns []
  (redis/start-conn-pool)
  (db/start-conn-pool)
  (logger/merge-config! {:timestamp-opts
                         {:timezone
                          (java.util.TimeZone/getTimeZone "Asia/Kolkata")}}))

(defn- stop-conns []
  (redis/stop-conn-pool)
  (db/stop-conn-pool))

(def reloadable-handler (wrap-reload #'app-handler))

(defn -main []
  (let [{:keys [host port threads]} (config)]
    (start-conns)
    (logger/infof "Running server at %s:%s" host port)
    (jetty/run-jetty app-handler {:port                 port
                                  :min-threads          threads
                                  :max-threads          threads
                                  :join?                false
                                  :send-server-version? false})))