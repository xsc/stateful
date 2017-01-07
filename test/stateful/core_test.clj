(ns stateful.core-test
  (:require [clojure.test.check
             [generators :as gen]
             [properties :as prop]
             [clojure-test :refer [defspec]]]
            [clojure.test.check :as tc]
            [clojure.test :refer :all]
            [stateful.core :as stateful]))

;; ## Basic Behaviour

(def ascending-integers
  (stateful/pure-generator
    (gen/vector
      (gen/let [delta    gen/s-pos-int
                previous (stateful/value [:previous])]
        (let [value (+ delta previous)]
          (stateful/return value {:previous value}))))
    {:previous 0}))

(defspec t-stateful-generator-behaviour 200
  (prop/for-all
    [asc-ints ascending-integers]
    (and (every? integer? asc-ints)
         (distinct? asc-ints)
         (or (not (next asc-ints))
             (apply <= asc-ints)))))

(deftest t-stateful-generator-shrinking
  (testing "simple shrinking case."
    (let [prop (prop/for-all
                 [asc-ints ascending-integers]
                 (not-any? #{100} asc-ints))
          result (is (tc/quick-check 200 prop))]
      (is (false? (:result result)))
      (is (= [[100]] (-> result :shrunk :smallest)))))
  (testing "shrinking case for combination of stateful generators."
    (let [prop (prop/for-all
                 [ints (->> (gen/vector ascending-integers)
                            (gen/fmap #(reduce into %)))]
                 (let [freq (frequencies ints)]
                   (<= (freq 100 0) 2)))
          result (is (tc/quick-check 200 prop))]
      (is (false? (:result result)))
      (is (= [[100 100 100]] (-> result :shrunk :smallest))))))
