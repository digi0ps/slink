(ns slink.helpers.response)


(defn success [data]
  {:status 200
   :body   {
            :success true
            :data    data
            }})

(defn error [status error]
  {:status status
   :body   {
            :success false
            :error   error
            }})