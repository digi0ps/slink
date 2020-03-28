(ns slink.core-test
  (:require [clojure.test :refer :all]
            [slink.core :refer :all]
            [slink.db.core :as db]
            [toucan.util.test :refer :all]
            [clojure.data.json :as j]))


(defn db-fixture [f]
  (println "Setting up database...")
  @db/setup-db
  (f)
  (println "Tearing down..."))

(use-fixtures :once db-fixture)

(defn- json [obj]
  (j/write-str obj))

(deftest app-handler-test
  (testing "testing / flow"
    (let [request {:request-method :get :uri "/"}
          response (app-handler request)
          expected (json {:message "server is running..."})]
      (is 200 (:status response))
      (println (type expected) (type (:body response)))
      (is (= expected (:body response)))
      ))

  (testing "testing sad cases for links flow"
    (testing "when user-id is not provided"
      (let [request {:request-method :get :uri "/api/links"}
            response (app-handler request)
            expected-body (json {:success false
                                 :error "User parameter is required."})]
        (testing "status should be 404"
          (is (= 404 (:status response))))
        (testing "body should be expected"
          (is (= expected-body (:body response))))))

    (testing "when user-id is not a string"
      (let [request {:request-method :get
                     :uri "/api/links"
                     :query-string "user=sdfsdf"}
            response (app-handler request)
            expected-body (json {:success false
                                 :error "User parameter must be an integer."})]
        (testing "status should be 500"
          (is (= 500 (:status response))))
        (testing "body should be expected"
          (is (= expected-body (:body response))))))

    (testing "when no links exist for the user"
      (let [request {:request-method :get
                     :uri "/api/links"
                     :query-string "user=12234543"}
            response (app-handler request)
            expected-body (json {:success true
                                 :data []})]
        (testing "status should be 404"
          (is (= 200 (:status response))))
        (testing "body should be expected"
          (is (= expected-body (:body response))))))))