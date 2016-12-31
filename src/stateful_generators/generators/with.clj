(ns stateful-generators.generators.with
  (:require [stateful-generators.core.protocols :as p]))

(defmacro with
  "Create a stateful generator that accesses the current state value using
   `binding`.

   ```clojure
   (def decreasing-int
     (gen/with [{:keys [previous-value] :as state}]
       (gen/let [value (if previous-value
                         (gen/fmap #(mod % previous-value) gen/pos-int)
                         gen/pos-int)]
         (gen/return
           (assoc state :previous-value value)
           value))))
   ```

   The above example will produce a decreasing sequence of integers:

   ```clojure
   (gen/bound (gen/vector decreasing-int))
   ```
   "
  [[binding] & body]
  `(p/construct
     (fn [~binding]
       ~@body)))
