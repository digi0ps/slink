(ns slink.helpers.response)


(defn success
  ([data]
   {:status 200
    :body   {
             :success true
             :data    data
             }})
  ([]
   (success {})))

(defn error [status error]
  {:status status
   :body   {
            :success false
            :error   error
            }})

(defn not-found
  ([_]
   {:status 404
    :body   ""})
  ([]
   (not-found {})))