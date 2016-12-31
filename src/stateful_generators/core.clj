(ns stateful-generators.core
  (:refer-clojure :exclude
                  [int vector list hash-map map keyword
                   char boolean byte bytes sequence
                   shuffle not-empty symbol namespace
                   set sorted-set uuid double let])
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

;; ## Facade

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
   with
   with-scope])

;; ## Imports

(import-vars
  [clojure.test.check.generators
   ;; dev helpers
   sample sample-seq generate

   ;; sequence input
   elements shuffle

   ;; scalar types
   nat int pos-int neg-int s-pos-int s-neg-int
   large-integer* large-integer
   double* double ratio
   char char-ascii char-alphanumeric char-alpha
   string string-ascii string-alphanumeric
   keyword keyword-ns symbol symbol-ns boolean
   byte bytes
   uuid])

;; ## Example

(comment
  (def ascending-integers
    (with-scope {:previous 0}
      (vector
        (with [{:keys [previous]}]
          (let [value (fmap #(+ previous %) s-pos-int)]
            (return {:previous value} value))))))
  (gen/sample (bound ascending-integers)))
