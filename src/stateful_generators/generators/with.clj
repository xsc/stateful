(ns stateful-generators.generators.with
  (:require [stateful-generators.core.protocols :as p]))

(defmacro with
  "Create a stateful generator that accesses the current state value using
   `binding`.

   ```clojure
   (def ascending-integers
     (gen/with-scope {:previous 0}
       (gen/vector
         (gen/with [{:keys [previous]}]
           (gen/let [value (gen/fmap #(+ previous %) gen/s-pos-int)]
             (gen/return {:previous value} value))))))
   ```

   The above example will produce a decreasing sequence of integers:

   ```clojure
   (gen/sample (gen/bound ascending-integers))
   ```
   "
  [[binding] & body]
  `(p/construct
     (fn [~binding]
       ~@body)))

(defmacro with-scope
  "Create a stateful generator that merges `scope` into the current state
   before calling the generator produced by `body`.

   See the docstring of [[with]] for example usage."
  [scope & body]
  `(let [gen#   (do ~@body)
         scope# ~scope]
     (p/construct
       (fn [state#]
         (->> (merge state# scope#)
              (p/stateful gen#))))))
