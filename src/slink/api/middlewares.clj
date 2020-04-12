(ns slink.api.middlewares
  (:require [slink.helpers.response :as res]))

(defn wrap-content-type-json [handler]
  (fn [request]
    (let [response (handler request)
          headers (:headers response)]
      (if (and
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
        (res/error 500 (.getMessage e))))))