(ns stateful-generators.generators.bind
  (:require [clojure.test.check.generators :as gen]
            [stateful-generators.core
             [box :as box]
             [protocols :as p]]))

(defn bind
  "Using the unboxed value produced by stateful generator `gen`, use `f` to
   create another stateful generator."
  [gen f]
  (p/construct
    (fn [state]
      (-> (p/stateful gen state)
          (gen/bind
            (fn [result]
              (p/stateful
                (f (box/value result))
                (box/state result))))))))
