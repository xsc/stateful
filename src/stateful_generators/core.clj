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
   any
   any-printable
   boolean
   byte
   bytes
   char
   char-alpha
   char-alphanumeric
   char-ascii
   choose
   ;; container-type
   double
   double*
   elements
   generate
   generator?
   ;; hash-map
   int
   keyword
   keyword-ns
   large-integer
   large-integer*
   lazy-random-states
   ;; list
   ;; list-distinct
   ;; list-distinct-by
   ;; map
   nat
   neg-int
   ;; no-shrink
   ;; not-empty
   ;; one-of
   pos-int
   ratio
   ;; recursive-gen
   ;; resize
   s-neg-int
   s-pos-int
   sample
   sample-seq
   ;; scale
   ;; set
   ;; shrink-2
   shuffle
   simple-type
   simple-type-printable
   ;; sized
   ;; sorted-set
   string
   string-alphanumeric
   string-ascii
   ;; such-that
   symbol
   symbol-ns
   uuid
   ;; vector-distinct
   ;; vector-distinct-by
   ])

;; ## Example

(comment
  (def ascending-integers
    (with-scope {:previous 0}
      (vector
        (with [{:keys [previous]}]
          (let [value (fmap #(+ previous %) s-pos-int)]
            (return {:previous value} value))))))
  (gen/sample (bound ascending-integers)))
