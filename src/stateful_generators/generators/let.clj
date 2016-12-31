(ns stateful-generators.generators.let
  (:refer-clojure :exclude [let])
  (:require [clojure.test.check.generators :as gen]
            [clojure.core :as core]
            [stateful-generators.core
             [box :as box]
             [protocols :as p]]
            [stateful-generators.generators
             [with :refer [with]]]))

(defmacro let
  "Create a stateful generator akin to `clojure.test.check.generators/let`,
   maintaining state through the `let`-bindings and body."
  [bindings & body]
  {:pre [(even? (count bindings))]}
  (core/let [pairs          (partition 2 bindings)
             state-sym      (gensym "state-")
             binding-syms   (repeatedly (count pairs) gensym)
             state-forms    (->> (map #(list `box/state %) binding-syms)
                                 (cons state-sym))]
    `(with [~state-sym]
       (gen/let
         [~@(mapcat
              (fn [[binding generator] binding-sym state-form]
                (list binding-sym
                      `(p/stateful ~generator ~state-form)
                      binding
                      `(gen/return (box/value ~binding-sym))))
              pairs
              binding-syms
              state-forms)]
         (p/stateful (do ~@body) ~(last state-forms))))))
