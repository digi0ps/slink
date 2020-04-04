(ns slink.api.routes
  (:require [slink.api.handler :refer :all]
            [reitit.ring :as ring]))

(def router
  (ring/router
    [["/" {:get hello-handler}]
     ["/api"
      ["/links" {:get user-links-handler
                 :put create-link-handler}]]]))