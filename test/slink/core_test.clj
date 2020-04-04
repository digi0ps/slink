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
                                 :error   "User parameter is required."})]
        (testing "status should be 404"
          (is (= 404 (:status response))))
        (testing "body should be expected"
          (is (= expected-body (:body response))))))

    (testing "when user-id is not a string"
      (let [request {:request-method :get
                     :uri            "/api/links"
                     :query-string   "user=sdfsdf"}
            response (app-handler request)
            expected-body (json {:success false
                                 :error   "User parameter must be an integer."})]
        (testing "status should be 500"
          (is (= 500 (:status response))))
        (testing "body should be expected"
          (is (= expected-body (:body response))))))

    (testing "when no links exist for the user"
      (let [request {:request-method :get
                     :uri            "/api/links"
                     :query-string   "user=12234543"}
            response (app-handler request)
            expected-body (json {:success true
                                 :data    []})]
        (testing "status should be 404"
          (is (= 200 (:status response))))
        (testing "body should be expected"
          (is (= expected-body (:body response)))))))

  (testing "creating a link flow"
    (testing "happy flow"
      (db/clear-all!)
      (let [request {:params         {:user 1234567
                                      :url  "https://google.com/big"}
                     :headers        {"content-type" "application/json"
                                      "host"         "localhost:5000"}
                     :request-method :put
                     :scheme         "http"
                     :uri            "/api/links"}
            response (app-handler request)
            body (j/read-str (:body response) :key-fn keyword)
            regex #"http:\/\/localhost:5000\/[a-zA-Z0-9]{6}"]
        (testing "response should be success"
          (is (= 200 (:status response)))
          (is (= true (:success body))))
        (testing "slink should match regex"
          (is (not (nil? (re-matches regex (:slink (:data body)))))))
        (testing "/links should return this link for the user"
          (let [request-2 {:request-method :get
                           :uri            "/api/links"
                           :query-string   "user=1234567"}
                response-2 (app-handler request-2)
                body-2 (j/read-str (:body response-2) :key-fn keyword)]
            (testing "response should be successs"
              (is (= 200 (:status response-2)))
              (is (= true (:success body-2))))
            (testing "count of data must be one"
              (is (= 1 (count (:data body-2)))))
            (testing "received link must contain correct details"
              (let [slink (first (:data body-2))]
                (is (= "https://google.com/big" (:url slink)))
                (is (= 1234567 (:user-id slink)))))))))))