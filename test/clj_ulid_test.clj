(ns clj-ulid-test
  (:require [clojure.test :refer :all]
            [clj-ulid :refer :all]))

(defn take-time-part [s]
  (subs s 0 10))


(deftest base32-encoding
  (testing "base32 encoding"
    (is (= "01aryz6s41" (encode 1469918176385 10))))
  (testing "base32 encoding with longer length"
    (is (= "0001as99aa60" (encode 1470264322240 12))))
  (testing "base32 encoding with truncated output"
    (is (= "as4y1e11" (encode 1470118279201 8))))
  (testing "base32 encoding with negative input"
    (is (= "00000000" (encode -23 8)))))


(deftest random-base32
  (testing "default length is 16-characters long"
    (is (= 16 (count (random-base32-string)))))
  (testing "variable length"
    (is (= 34 (count (random-base32-string 34))))))



(deftest time-encoding
  (testing "time is encoded"
    (is (= "01aryz6s41" (take-time-part (ulid 1469918176385)))))
  (testing "time can be decoded from ULID"
    (is (= 1469918176381 (ulid->timestamp (ulid 1469918176381)))))
  (testing "time 0 ULID is possible"
    (is (= 0 (ulid->timestamp (ulid 0)))))
  (testing "decoding ULID works with time 0"
    (is (= 0 (ulid->timestamp "0000000000vqwvw9f06x9bkxkc"))))
  (testing "decoding ULID works with time 100"
    (is (= 100 (ulid->timestamp "0000000034d5fjgp3k55r4amrt")))))

(deftest ulid-properties
  (testing "ulid has length of 26"
    (is (= 26 (count (ulid))))))
