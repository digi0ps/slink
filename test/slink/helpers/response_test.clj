(ns slink.helpers.response-test
  (:require [clojure.test :refer :all])
  (:require [slink.helpers.response :refer :all]))

(deftest success-test
  (testing "should return the correct success response when given the data"
    (let [expected-response {:status 200 :body {:success true :data {:name "slink"}}}]
      (is (= expected-response (success {:name "slink"}))))))

(deftest error-test
  (testing "should return the correct error and status"
    (let [expected-body {:success false :error "Failed"}
          response (error 500 "Failed")]
      (is (= 500 (:status response)))
      (is (= expected-body (:body response)))0)))
