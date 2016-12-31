(ns stateful-generators.core.protocols
  (:require [stateful-generators.utils.potemkin :refer [defprotocol+]]
            [stateful-generators.core.box :as box]
            [clojure.test.check.generators :as gen]))

;; ## Protocol

(defprotocol+ StatefulGenerator
  (stateful [gen state]
    "Make the given generator stateful, by binding it to the given state
     and ensuring it only returns boxed values."))

;; ## Default Implementations

(extend-protocol StatefulGenerator
  clojure.test.check.generators.Generator
  (stateful [gen state]
    (gen/fmap
      (fn [value]
        (if-not (box/box? value)
          (box/box state value)
          value))
      gen))

  Object
  (stateful [value state]
    (gen/return (box/box state value)))

  nil
  (stateful [_ state]
    (gen/return (box/box state nil))))

;; ## Derived Functions

(defn bound
  "Bind the given stateful generator to the given state. Will produce
   a plain, unboxed value."
  ([generator] (bound generator {}))
  ([generator state]
   (gen/fmap box/value (stateful generator state))))

(defn bound-boxed
  "Bind the given stateful generator to the given state. Will produce
   a boxed value."
  ([generator] (bound-boxed generator {}))
  ([generator state]
   (stateful generator state)))

(defn construct
  "Given a function returning a generator, produce a `StatefulGenerator`
   that can be [[bound]] to a state."
  [f]
  (reify StatefulGenerator
    (stateful [_ state]
      (gen/let [value (stateful (f state) state)]
        (if-not (box/box? value)
          (box/box state value)
          value)))))
