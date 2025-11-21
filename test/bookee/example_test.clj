(ns bookee.example-test
  (:require [clojure.test :refer [deftest is testing]]))

(deftest example-passing-test
  (testing "Basic arithmetic"
    (is (= 4 (+ 2 2)))
    (is (= 6 (* 2 3)))))

(deftest example-data-test
  (testing "Data structures"
    (is (= [:a :b :c] (vec '(:a :b :c))))
    (is (= #{1 2 3} (set [1 2 3 2 1])))))
