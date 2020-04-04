(ns slink.helpers.response-test
  (:require [clojure.test :refer :all])
  (:require [slink.helpers.response :refer :all]))

(deftest success-test
  (testing "should return the correct success response when given the data"
    (let [expected-response {:status 200 :body {:success true :data {:name "slink"}}}]
      (is (= expected-response (success {:name "slink"})))))
  (testing "should return empty success response when no arg is passed"
    (is (= {:status 200 :body {:success true :data {}}} (success)))))

(deftest error-test
  (testing "should return the correct error and status"
    (let [expected-body {:success false :error "Failed"}
          response (error 500 "Failed")]
      (is (= 500 (:status response)))
      (is (= expected-body (:body response))))))

(deftest not-found-test
  (let [expected {:status 404 :body ""}]
    (testing "should return 404 with no args"
      (is (= expected (not-found))))

    (testing "should return 404 with args"
      (is (= expected (not-found {:params {:name "ops"}}))))))
