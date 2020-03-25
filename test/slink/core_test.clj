(ns slink.core-test
  (:require [clojure.test :refer :all]
            [slink.core :refer :all]))

(deftest app-handler-test
  (testing "should return constant message"
    (let [request {:request-method :get :uri "/"}
          response (app-handler request)]
      (is 200 (:status response))
      (println response)
      ; (is (= "server is running..." ((:body response) "message")))
      )))