(ns slink.domain.slack
  (:require [slink.config :refer [config]]
            [clj-http.client :as http]))

(defn- get-payload-field
  ([title value short]
   {:title title
    :value value
    :short short})
  ([title value]
   (get-payload-field title value true)))

(defn- post-slack-request-webhook [payload]
  (http/post
    (config :slack :request-webhook)
    {:form-params      {:attachments [payload]}
     :throw-exceptions false
     :content-type     :json}))

(defn report-request-error [request err]
  (let [err-msg (.getMessage err)
        formatted-error (format "ERROR: %s" err-msg)
        {:keys [remote-addr uri query-string]} request
        url (clojure.string/join "?" [uri query-string])
        payload {:fallback formatted-error
                 :color    "#ff8000"
                 :fields   [(get-payload-field "Error" err-msg false)
                            (get-payload-field "IP Address" remote-addr)
                            (get-payload-field "Requested URL" url)]}]
    (post-slack-request-webhook payload)))