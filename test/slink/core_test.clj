(ns slink.core-test
  (:require [clojure.test :refer :all]
            [slink.core :refer :all]
            [slink.db.core :as db]
            [toucan.util.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as j]
            [slink.db.redis :as redis]))


(defn db-fixture [f]
  (println "Setting up database...")
  (db/start-conn-pool)
  (redis/start-conn-pool)
  (f)
  (db/stop-conn-pool)
  (redis/stop-conn-pool)
  (println "Tearing down..."))

(use-fixtures :once db-fixture)

(defn- to-json [obj]
  (j/write-str obj))

(defn from-json [obj]
  (j/read-str obj :key-fn keyword))

(deftest app-handler-test
  (testing "testing / flow"
    (let [request (mock/request :get "/")
          response (app-handler request)
          expected (to-json {:message "server is running..."})]
      (is 200 (:status response))
      (println (type expected) (type (:body response)))
      (is (= expected (:body response)))
      ))

  (testing "testing sad cases for links flow"
    (testing "when user-id is not provided"
      (let [request (mock/request :get "/api/links")
            response (app-handler request)
            expected-body (to-json {:success false
                                    :error   "User parameter is required."})]
        (testing "status should be 404"
          (is (= 404 (:status response))))
        (testing "body should be expected"
          (is (= expected-body (:body response))))))

    (testing "when user-id is not a string"
      (let [request (-> (mock/request :get "/api/links")
                        (mock/query-string {:user "sdfadf"}))
            response (app-handler request)
            expected-body (to-json {:success false
                                    :error   "User parameter must be an integer."})]
        (testing "status should be 500"
          (is (= 500 (:status response))))
        (testing "body should be expected"
          (is (= expected-body (:body response))))))

    (testing "when no links exist for the user"
      (let [request (-> (mock/request :get "/api/links")
                        (mock/query-string {:user 12234543}))
            response (app-handler request)
            expected-body (to-json {:success true
                                    :data    []})]
        (testing "status should be 404"
          (is (= 200 (:status response))))
        (testing "body should be expected"
          (is (= expected-body (:body response)))))))

  (testing "creating a link flow"
    (testing "happy flow"
      (db/clear-all!)
      (let [request (-> (mock/request :put "http://localhost:5000/api/links")
                        (mock/json-body {:user 1234567 :url "https://web.site"}))
            response (app-handler request)
            body (j/read-str (:body response) :key-fn keyword)
            regex #"http:\/\/localhost:5000\/[a-zA-Z0-9]{6}"]
        (testing "response should be success"
          (is (= 200 (:status response)))
          (is (= true (:success body))))
        (testing "slink should match regex"
          (println "DATA" (:data body))
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
                (is (= "https://web.site" (:url slink)))
                (is (= 1234567 (:user-id slink)))))))))

    (testing "when the url is not a website link, should say URL is not valid"
      (db/clear-all!)
      (let [request (-> (mock/request :put "http://localhost:5000/api/links")
                        (mock/json-body {:user 1234567 :url "adfadfasxdf"}))
            response (app-handler request)
            expected-body (to-json {:success false
                                       :error   "URL is not valid."})]
        (testing "status must be 404"
          (is (= 404 (:status response))))

        (testing "body must be expected"
          (is (= expected-body (:body response)))))))

  (testing "redirect flow"
    (db/clear-all!)
    (testing "happy flow"
      (let [req (-> (mock/request :put "/api/links")
                    (mock/json-body {:user 1234567 :url "https://google.com"}))
            res (app-handler req)
            slink (get-in (from-json (:body res)) [:data :slink])]
        (testing "link should be created"
          (is (= 200 (:status res)))
          (is (not (nil? slink))))
        (let [request (mock/request :get slink)
              response (app-handler request)]
          (testing "it should be a redirect"
            (is (= 302 (:status response)))
            (is (= "https://google.com" ((:headers response) "Location")))))))

    (testing "sad flow"
      (db/clear-all!)
      (let [request (mock/request :get "/noeafds")
              response (app-handler request)]
          (testing "it should be a 404"
            (is (= 404 (:status response)))
            (is (= "" (:body response))))))))