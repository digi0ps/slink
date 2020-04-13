(ns slink.api.middlewares
  (:require [slink.helpers.response :as res]
            [slink.domain.slack :as slack]
            [slink.config :refer [config]]
            [clojure.string :as s]))

(defn wrap-content-type-json [handler]
  (fn [request]
    (let [response (handler request)
          headers (:headers response)]
      (if (and
            headers
            (headers "Content-Type")
            (not (= (headers "Content-Type") "application/octet-stream")))
        response
        (let [modified-headers (assoc headers "Content-Type" "application/json; charset=utf-8")]
          (assoc response :headers modified-headers))))))


(defn wrap-exceptions [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (println "EXCEPTION: " (.getMessage e))
        (when (= "prod" (config :env))
          (slack/report-request-error request e))
        (res/error 500 "Server error has occured.")))))


(defn wrap-cors [handler]
  (fn [request]
    (let [response (handler request)
          allow-origins (s/join "," (config :cors :domains))
          cors-headers (assoc (:headers response) "Access-Control-Allow-Origin" allow-origins)]
      (if (= :options (:request-method request))
        (let [options-headers (->
                                cors-headers
                                (dissoc "Access-Control-Request-Method")
                                (assoc "Access-Control-Allow-Methods" "OPTIONS, GET, PUT")
                                (assoc "Access-Control-Allow-Headers" "Content-Type"))]
          (assoc response :headers options-headers))
        (assoc response :headers cors-headers)))))