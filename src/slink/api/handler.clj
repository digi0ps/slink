(ns slink.api.handler)

(defn hello-handler [request]
  {:status 200
   :body {:message "server is running..."}})