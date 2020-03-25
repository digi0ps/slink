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

  (testing "should overwrite Content-Type  header"
    (let [mw-attached-handler (mw/wrap-content-type-json identity)
          request {:method "get" :params {} :headers {"Content-Type" "text/plain"}}
          expected-response {:method "get"
                            :params {}
                            :headers {"Content-Type" "application/json; charset=utf-8"}}
          response (mw-attached-handler request)]
      (is (= expected-response response)))))
