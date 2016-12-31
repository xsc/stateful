(ns stateful-generators.generators.return
  (:require [stateful-generators.core
             [box :as box]
             [protocols :as p]]
            [clojure.test.check.generators :as gen]))

(defn return
  "Create a stateful generator that will return the given `value` and merge
   the given `state` into the current one."
  ([value]
   (p/construct
     (fn [current-state]
       (gen/return
         (box/box current-state value)))))
  ([state value]
   (p/construct
     (fn [current-state]
       (gen/return
         (box/box
           (merge current-state state)
           value))))))
