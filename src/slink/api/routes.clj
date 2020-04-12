(ns slink.api.routes
  (:require [slink.api.handler :refer :all]
            [reitit.ring :as ring]
            [reitit.ring.spec :as rs]
            [reitit.swagger :as swagger]
            [reitit.coercion.spec :as spec]
            [reitit.swagger-ui :as swagger-ui]))

(def router
  (ring/router
    [["/"
      ["" {:get hello-handler}]
      ["swagger.json" {:no-doc      true
                       :swagger     {:info     {:title "Slink API"}
                                     :basePath ""}
                       :get         (swagger/create-swagger-handler)
                       :conflicting true}]
      ["swagger/docs/*" {:no-doc      true
                         :get         (swagger-ui/create-swagger-ui-handler)
                         :conflicting true}]]
     ["/api"
      ["/links" {:get {:summary    "Get all short links for an user."
                       :parameters {:query {:user int?}}
                       :handler    user-links-handler}
                 :put {:summary "Create a short link."
                       :parameters {:body {:url string?
                                           :user int?}}
                       :handler create-link-handler}}]]
     ["/:hash" {:get         {:summary "Redirects a short link to it's original url."
                              :parameters {:path {:hash string?}}
                              :handler redirect-link-handler}
                :conflicting true}]]
    {:validate rs/validate
     :data     {:coercion   spec/coercion}}))