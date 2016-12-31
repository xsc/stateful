(ns stateful-generators.core
  (:refer-clojure :exclude [let vector])
  (:require [clojure.test.check.generators :as gen]
            [stateful-generators.utils.potemkin :refer [import-vars]]
            [stateful-generators.core
             protocols]
            [stateful-generators.generators
             bind
             fmap
             let
             return
             tuple
             vector
             with]))

;; ## Import

(import-vars
  [stateful-generators.core.protocols
   bound
   bound-boxed
   construct]

  [stateful-generators.generators.bind
   bind]

  [stateful-generators.generators.fmap
   fmap]

  [stateful-generators.generators.let
   let]

  [stateful-generators.generators.return
   return]

  [stateful-generators.generators.tuple
   tuple]

  [stateful-generators.generators.vector
   vector]

  [stateful-generators.generators.with
   with])

;; ## Example

(comment
  (def ascending-integers
    (vector
      (with [{:keys [previous]}]
        (let [value (fmap #(+ previous %) gen/s-pos-int)]
          (return {:previous value} value)))))
  (gen/sample (bound ascending-integers {:previous 1})))
