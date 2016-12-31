(ns stateful-generators.generators.vector
  (:refer-clojure :exclude [vector])
  (:require [clojure.test.check.generators :as gen]
            [stateful-generators.generators
             [bind :as stateful-bind]
             [tuple :refer [tuple]]]))

(defn vector
  "Create a stateful generator that will produce a vector of elements where each
   element is generated using the given generator. Subsequent elements will
   have access to the state produced by previous ones."
  ([gen num-elements]
   (apply tuple (repeat num-elements gen)))
  ([gen]
   (stateful-bind/bind
     (gen/sized #(gen/choose 0 %))
     #(vector gen %)))
  ([gen min-elements max-elements]
   (stateful-bind/bind
     (gen/choose min-elements max-elements)
     #(vector gen %))))
