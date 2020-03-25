(ns slink.api.middlewares)

(defn wrap-content-type-json [handler]
  (fn [request]
    (let [response (handler request)
          headers (:headers response)
          modified-headers (assoc headers "Content-Type" "application/json; charset=utf-8")]
      (assoc response :headers modified-headers))))