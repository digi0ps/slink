(ns slink.api.routes
  (:require [slink.api.handler :refer :all]
            [reitit.ring :as ring]
            [reitit.ring.spec :as rs]))

(def router
  (ring/router
    [["/" {:get hello-handler}]
     ["/api"
      ["/links" {:get user-links-handler
                 :put create-link-handler}]]
     ["/:hash" {:get redirect-link-handler}]]
    {:validate rs/validate}))