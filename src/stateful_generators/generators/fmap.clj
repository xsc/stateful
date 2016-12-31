(ns stateful-generators.generators.fmap
  (:require [clojure.test.check.generators :as gen]
            [stateful-generators.core
             [box :as box]
             [protocols :as p]]))

(defn fmap
  "Call `f` on the unboxed value produced by a stateful generator `gen`."
  [f gen]
  (p/construct
    (fn [state]
      (gen/fmap
        (fn [result]
          (box/box
            (box/state result)
            (f (box/value result))))
        (p/stateful gen state)))))
