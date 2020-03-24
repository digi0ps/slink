(ns slink.api.handler)

(defn hello-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "hello from clojure"})