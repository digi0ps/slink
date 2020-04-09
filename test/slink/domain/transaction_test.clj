(ns slink.domain.transaction-test
  (:require [clojure.test :refer :all])
  (:require [slink.domain.transaction :refer [get-url-for-hash]]
            [slink.db.core :as db]
            [slink.db.redis :as redis]))

(deftest get-url-for-hash-test
  (testing "when link is present in redis"
    (let [is-db-fn-called? (atom false)]
      (with-redefs [redis/fetch-url-from-redis (constantly "redis-url")
                    db/fetch-link-by-hash (fn [& args]
                                            (reset! is-db-fn-called? true))]
        (let [link (get-url-for-hash "abcdef")]
          (is (= "redis-url" link))
          (is (= false @is-db-fn-called?))))))
  (testing "when link is not present in redis but in db"
    (let [is-redis-set-called? (atom false)]
      (with-redefs [redis/fetch-url-from-redis (constantly nil)
                    redis/save-hash-with-url (fn [& args]
                                               (reset! is-redis-set-called? true))
                    db/fetch-link-by-hash (constantly {:url "db-url"})]
        (let [link (get-url-for-hash "abcdef")]
          (is (= "db-url" link))
          (is (= true @is-redis-set-called?))))))
  (testing "when link is not present in both"
    (let [is-redis-set-called? (atom false)]
      (with-redefs [redis/fetch-url-from-redis (constantly nil)
                    db/fetch-link-by-hash (constantly nil)
                    redis/save-hash-with-url (fn [& args]
                                               (reset! is-redis-set-called? true))]
        (let [link (get-url-for-hash "abcdef")]
          (is (= nil link))
          (is (= false @is-redis-set-called?)))))))
