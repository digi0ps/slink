(ns slink.api.handler-test
  (:require [clojure.test :refer :all])
  (:require [slink.api.handler :refer [user-links-handler]]
            [clj-time.core]
            [slink.db.core :as db]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

(deftest user-links-handler-test
  (testing "when user-id is not passed"
    (let [request {:params {}}
          response (user-links-handler request)
          expected-body {:success false :error "User parameter is required."}]
      (testing "status should be 404"
        (is (= 404 (:status response))))
      (testing "body should be contain error"
        (is (= expected-body (:body response))))))

  (testing "when user-id is not an integer, should throw exception"
    (let [request {:params {:user "sdfadsf"}}]
      (try
        (user-links-handler request)
        (catch Exception e
          (is (= "User parameter must be an integer." (.getMessage e)))))))

  (testing "when there are no links for the user, "
    (let [request {:params {:user "12345"}}
          expected-response {:status 200 :body {:success true :data []}}]
      (with-redefs [db/fetch-all-links-for-user (constantly [])]
        (is (= expected-response (user-links-handler request))))))

  (testing "when there are links for the user, "
    (let [request {:params {:user "12345"}}
          time (t/now)
          expected-body {:success true :data [{:hash    "abc" :url "google.com"
                                               :user-id 12345 :created-at (c/to-epoch time)}]}]
      (with-redefs [db/fetch-all-links-for-user (constantly
                                                  [{:hash    "abc" :url "google.com"
                                                    :user-id 12345 :created-at time}])]
        (let [response (user-links-handler request)]
          (testing "status should be 200"
            (is (= 200 (:status response))))
          (testing "body should be as expected"
            (is (= expected-body (:body response)))))))))
