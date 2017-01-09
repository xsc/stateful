(ns stateful.core
  (:require [stateful.dynamic :as dynamic]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.rose-tree :as rose]))

;; ## State

(def ^:private ^:dynamic *state*
  ::none)

(defn- assert-stateful!
  []
  (when (= *state* ::none)
    (throw
      (IllegalStateException.
        (str "A stateful generator was called within a non-stateful "
             "generator.\n"
             "Please use 'stateful.core/generator' to wrap the generator.")))))

;; ## Stateful Generator

(defn generator
  "Make the given generator stateful, allowing it access to an implicit state
   map including all changes made during the run.

   ```clojure
   (gen/generate
     (stateful/generator gen/int))
   ;; => 0
   ```

   The output of the stateful generator will be the same as the one it wraps,
   but you can use the [[state]] and [[value]] generators to access the current
   state map.

   ```clojure
   (gen/generate
     (stateful/generator
       (gen/tuple gen/int (stateful/state))))
   ;; => [0 {}]
   ```

   Additionally, you can supply an initial state value:

   ```clojure
   (gen/generate
     (stateful/generator
       (gen/tuple gen/int (stateful/state))
       {:number-of-elements 0}))
   ;; => [0 {:number-of-elements 0}]
   ```

   Note that the scopes of nested stateful generators merge, i.e.:

   ```clojure
   (gen/generate
     (stateful/generator
       (gen/tuple
         (stateful/generator gen/int {:int? true})
         (stateful/generator gen/string {:string? true})
         (stateful/state))))
   ;; => [0 \"\" {:int? true, :string? true}]
   ```

   The state can be manipulated using the [[return*]], [[return]], etc...
   generators.

   Please note that shrinking efficiency will most likely suffer if you're
   using data added to the state by another generator."
  [{:keys [gen] :as generator} & [initial-state]]
  {:pre [(or (nil? initial-state)
             (map? initial-state))]}
  (let [gen' (bound-fn [rnd size]
               (cond (= *state* ::none)
                     (let [scope (or initial-state {})
                           tree (binding [*state* scope]
                                  (gen rnd size))]
                       (binding [*state* scope]
                         (dynamic/rose-tree tree)))

                     (empty? initial-state)
                     (gen rnd size)

                     :else
                     (let [scope (merge *state* initial-state)]
                       (set! *state* scope)
                       (gen rnd size))))]
    (assoc generator :gen gen')))

;; ## Access

(defn state
  "Generator that returns the full current state map.

   ```clojure
   (gen/generate
     (stateful/generator
       (gen/fmap keys (stateful/state))
       {:some :state}))
   ;; => [(:some) {:some state}]
   ```

   Use [[value]] to access a certain value using its path into the map."
  []
  (gen/->Generator
    (bound-fn current-state-lookup
      [_ _]
      (assert-stateful!)
      (rose/pure *state*))))

(defn value
  "Generator that looks up a path within the state map.

   ```clojure
   (gen/generate
     (stateful/generator
       (stateful/value [:some])
       {:some :state}))
   ;; => [:state {:some state}]
   ```

   Use [[state]] if you need access to the full state map."
  [ks & [default]]
  {:pre [(sequential? ks)]}
  (gen/fmap
    #(get-in % ks default)
    (state)))

;; ## Fmap/Bind

(defn fmap
  "Like test.check's `fmap` but uses a two parameter function passing both the
   generated value and the current state.

   ```clojure
   (stateful/fmap
     (fn [value {:keys [leaf-count]}]
       {:input-value         value
        :expected-leaf-count leaf-count})
     gen-tree)
   ```

   This function implicitly wraps `gen` with [[generator]]."
  [f gen]
  (generator
    (gen/fmap
      (fn [[value state]]
        (f value state))
      (gen/tuple gen (state)))))

(defn bind
  "Like test.check's `bind` but uses a two parameter function passing both the
   generated value and the current state.

   This function implicitly wraps `gen` with [[generator]]."
  [gen f]
  (generator
    (gen/bind
      (gen/tuple gen (state))
      (fn [[value state]]
        (f value state)))))

;; ## Return

(defn return*
  "Create a generator that updates the current state using the given function
   and arguments, before returning the given value.

   ```clojure
   (gen/generate
     (stateful/generator
       (stateful/return* 1 update :returned inc)
       {:returned 0}))
   ;; => [1 {:returned 1}]
   ```

   You can use [[return]], [[return-and-count]] or [[return-and-collect]] for
   some common use cases."
  [v f & args]
  (gen/fmap
    (fn [value]
      (assert-stateful!)
      (set! *state* (apply f *state* args))
      value)
    (gen/return v)))

(defn return
  "Like [[return*]], merging the given map into the current state."
  [v state]
  [{:pre [(map? state)]}]
  (return* v merge state))

(defn return-and-count
  "Like [[return*]], updating a counter within the current state."
  [v counter-key]
  (return* v update counter-key (fnil inc 0)))

(defn return-and-collect
  "Like [[return*]], updating a list of values within the current state."
  ([v collection-key]
   (return-and-collect v collection-key v))
  ([v collection-key collect-value]
   (return* v update collection-key (fnil conj []) collect-value)))
