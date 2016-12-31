(ns stateful-generators.core.box)

(defrecord Box [state value])

(defn box?
  "Check whether the given value is a value/state tuple."
  [value]
  (instance? stateful_generators.core.box.Box value))

(defn box
  "Generate a value/state tuple. If `value` is already a box, its state will
   be updated."
  [state value]
  {:pre [(map? state)]}
  (if (box? value)
    (assoc value :state state)
    (->Box state value)))

(defn value
  [^Box box]
  (.-value box))

(defn state
  [^Box box]
  (.-state box))
