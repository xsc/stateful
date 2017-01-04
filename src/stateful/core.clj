(ns stateful.core
  (:refer-clojure :exclude [vector])
  (:require [clojure.test.check.generators :as gen]
            [stateful.potemkin :refer [defprotocol+]]
            [clojure.core :as core])
  (:import [clojure.test.check.generators Generator]))

;; ## Protocol

(defprotocol+ Stateful
  (stateful-generator [gen]
    "Generate a stateful generator, producing a single _function_ taking a
     state map and returning a tuple of new state and value."))

;; ## Stateful Coercion

(defn- ->stateful
  [gen]
  {:pre [(gen/generator? gen)]}
  (assoc gen ::stateful? true))

(defn- stateful?
  [gen]
  (::stateful? gen))

(extend-protocol Stateful
  Generator
  (stateful-generator [gen]
    (if-not (stateful? gen)
      (->stateful
        (gen/fmap
          (fn [value]
            #(core/vector % value))
          gen))
      gen))

  Object
  (stateful-generator [value]
    (->stateful
      (gen/return #(core/vector % value))))

  nil
  (stateful-generator [_]
    (->stateful
      (gen/return #(core/vector % nil)))))

;; ## Main Functionality

(defn bound
  [gen state]
  {:pre [(stateful? gen)]}
  (gen/fmap (comp second #(% state)) gen))

(defn ^:no-doc generator*
  [f]
  (->stateful (gen/return f)))

;; ## Tuple/Vector

(defn- reduce-tuple
  [fn-tuple]
  (fn [state]
    (loop [state  state
           result []
           fns    fn-tuple]
      (if-let [[f & rst] (seq fns)]
        (let [[state' value] (f state)]
          (recur state' (conj result value) rst))
        [state result]))))

(defn tuple
  [& gens]
  (->> gens
       (map stateful-generator)
       (apply gen/tuple)
       (gen/fmap reduce-tuple)
       (->stateful)))

(defn vector
  [gen & args]
  (->> (apply gen/vector (stateful-generator gen) args)
       (gen/fmap reduce-tuple)
       (->stateful)))

;; ## Try it

(require '[clojure.test.check.properties :as prop]
         '[clojure.test.check.clojure-test :refer [defspec]])

(defspec t-example 200
  (prop/for-all
    [v (bound
         (vector
           (->stateful
             (gen/fmap
               (fn [delta]
                 (fn [state]
                   [(update state :x + delta) (+ (:x state) delta)]))
               gen/s-pos-int)))
         {:x 1})]
    (not-any? #{100} v)))
