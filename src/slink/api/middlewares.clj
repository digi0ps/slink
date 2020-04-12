(ns slink.api.middlewares
  (:require [slink.helpers.response :as res]
            [slink.domain.slack :as slack]))

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
        (slack/report-request-error request e)
        (res/error 500 "Server error has occured.")))))