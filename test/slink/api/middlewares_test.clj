(ns slink.api.middlewares-test
  (:require [clojure.test :refer :all])
  (:require [slink.api.middlewares :as mw]
            [slink.config :refer [config]]
            [slink.domain.slack :as slack]))

(deftest wrap-content-type-json-test
  (testing "should attach content type header when not present"
    (let [mw-attached-handler (mw/wrap-content-type-json identity)
          request {:method "get" :params {}}
          expected-response {:method  "get"
                             :params  {}
                             :headers {"Content-Type" "application/json; charset=utf-8"}}
          response (mw-attached-handler request)]
      (is (= expected-response response))))

  (testing "should not affect other headers"
    (let [mw-attached-handler (mw/wrap-content-type-json identity)
          request {:method "get" :params {} :headers {"Token" "random"}}
          expected-response {:method  "get"
                             :params  {}
                             :headers {"Token"        "random"
                                       "Content-Type" "application/json; charset=utf-8"}}
          response (mw-attached-handler request)]
      (is (= expected-response response))))

  (testing "should not overwrite Content-Type  header"
    (let [mw-attached-handler (mw/wrap-content-type-json identity)
          request {:method "get" :params {} :headers {"Content-Type" "text/plain"}}
          expected-response {:method  "get"
                             :params  {}
                             :headers {"Content-Type" "text/plain"}}
          response (mw-attached-handler request)]
      (is (= expected-response response))))

  (testing "should overwrite if content-type is octet-stream"
    (let [mw-attached-handler (mw/wrap-content-type-json identity)
          request {:method "get" :params {} :headers {"Content-Type" "application/octet-stream"}}
          expected-response {:method  "get"
                             :params  {}
                             :headers {"Content-Type" "application/json; charset=utf-8"}}
          response (mw-attached-handler request)]
      (is (= expected-response response)))))

(deftest wrap-exceptions-test
  (testing "when handler returns successfully, should return the response"
    (let [handler (constantly {:success true})
          mw-attached-handler (mw/wrap-exceptions handler)
          response (mw-attached-handler {})]
      (is (= {:success true} response))))

  (testing "when handler throws an error,"
    (testing "response should be "
      (let [handler (fn [_] (throw (Exception. "Dummy Error")))
            mw-attached-handler (mw/wrap-exceptions handler)
            response (mw-attached-handler {})]
        (testing "status should be 500"
          (is (= 500 (:status response))))
        (testing "body should say server error"
          (is (= false (:success (:body response))))
          (is (= "Server error has occured." (:error (:body response)))))))

    (testing "should not report to slack if env is dev"
      (let [handler (fn [_] (throw (Exception. "Dummy Error")))
            mw-attached-handler (mw/wrap-exceptions handler)
            is-slack-called? (atom false)]
        (with-redefs [config (constantly "test")
                      slack/report-request-error (fn [_ _]
                                                   (reset! is-slack-called? true))]
          (mw-attached-handler {})
          (is (= @is-slack-called? false)))))

    (testing "should report to slack if env is prod"
      (let [handler (fn [_] (throw (Exception. "Dummy Error")))
            mw-attached-handler (mw/wrap-exceptions handler)
            is-slack-called? (atom false)]
        (with-redefs [config (constantly "prod")
                      slack/report-request-error (fn [_ e]
                                                   (is (= (.getMessage e) "Dummy Error"))
                                                   (reset! is-slack-called? true))]
          (mw-attached-handler {})
          (is (= @is-slack-called? true)))))))

(deftest wrap-cors-test
  (testing "should return allow origin for *"
    (let [mw-attached-handler (mw/wrap-cors identity)]
      (with-redefs [config (constantly ["*"])]
        (let [response (mw-attached-handler {})
              headers (:headers response)]
          (is (= "*" (headers "Access-Control-Allow-Origin")))))))

  (testing "should return allow origin for multiple dowmains"
    (let [mw-attached-handler (mw/wrap-cors identity)]
      (with-redefs [config (constantly ["localhost" "slink.com"])]
        (let [response (mw-attached-handler {})
              headers (:headers response)]
          (is (= "localhost,slink.com" (headers "Access-Control-Allow-Origin")))))))

  (testing "should return allow-methods + allow-headers along with allow origin for options request"
    (let [mw-attached-handler (mw/wrap-cors identity)]
      (with-redefs [config (constantly ["*"])]
        (let [response (mw-attached-handler {:request-method :options})
              headers (:headers response)]
          (is (= "*" (headers "Access-Control-Allow-Origin")))
          (is (= "OPTIONS, GET, PUT" (headers "Access-Control-Allow-Methods")))
          (is (= "Content-Type" (headers "Access-Control-Allow-Headers"))))))))
