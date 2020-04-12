(ns slink.api.middlewares-test
  (:require [clojure.test :refer :all])
  (:require [slink.api.middlewares :as mw]))

(deftest wrap-content-type-json-test
  (testing "should attach content type header when not present"
    (let [mw-attached-handler (mw/wrap-content-type-json identity)
          request {:method "get" :params {}}
          expected-response {:method "get"
                            :params {}
                            :headers {"Content-Type" "application/json; charset=utf-8"}}
          response (mw-attached-handler request)]
      (is (= expected-response response))))

  (testing "should not affect other headers"
    (let [mw-attached-handler (mw/wrap-content-type-json identity)
          request {:method "get" :params {} :headers {"Token" "random"}}
          expected-response {:method "get"
                            :params {}
                            :headers {"Token" "random"
                                      "Content-Type" "application/json; charset=utf-8"}}
          response (mw-attached-handler request)]
      (is (= expected-response response))))

  (testing "should not overwrite Content-Type  header"
    (let [mw-attached-handler (mw/wrap-content-type-json identity)
          request {:method "get" :params {} :headers {"Content-Type" "text/plain"}}
          expected-response {:method "get"
                            :params {}
                            :headers {"Content-Type" "text/plain"}}
          response (mw-attached-handler request)]
      (is (= expected-response response))))

  (testing "should overwrite if content-type is octet-stream"
    (let [mw-attached-handler (mw/wrap-content-type-json identity)
          request {:method "get" :params {} :headers {"Content-Type" "application/octet-stream"}}
          expected-response {:method "get"
                            :params {}
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
    (let [handler (fn [_] (throw (Exception. "Dummy Error")))
          mw-attached-handler (mw/wrap-exceptions handler)
          response (mw-attached-handler {})]
      (testing "status should be 500"
        (is (= 500 (:status response))))
      (testing "body should contain the error"
        (is (= false (:success (:body response))))
        (is (= "Dummy Error" (:error (:body response))))))))
