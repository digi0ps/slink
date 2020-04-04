(ns slink.helpers.hashing-test
  (:require [clojure.test :refer :all])
  (:require [slink.helpers.hashing :refer [generate-hash]]
            [slink.config :refer [config]]))

(def ^:private ^:const test-url-1 "https://google.com")

(deftest generate-hash-test
  (testing "should return an hash of configured size"
    (let [hash (generate-hash 12345 test-url-1)]
      (is (= (config :hash :length) (count hash)))))

  (testing "hash should be different for same url by same user"
    (let [hash-1 (generate-hash 12345 test-url-1)
          _ (Thread/sleep 1)
          hash-2 (generate-hash 12345 test-url-1)]
      (is (not (= hash-1 hash-2))))))
