(ns stateful.dynamic
  "Utilities to handle dynamic variables in Rose tree children."
  (:require [clojure.test.check.rose-tree :as rose])
  (:import [clojure.test.check.rose_tree RoseTree]))

(defn- make-bound-head
  []
  (bound-fn [sq]
    (if (seq sq)
      (first sq)
      ::empty)))

(defn- bound-lazy-seq
  [sq]
  (let [head-of (make-bound-head)
        bound-seq (fn bound-seq
                    [sq]
                    (lazy-seq
                      (let [head (head-of sq)]
                        (when (not= head ::empty)
                          (cons head (bound-seq (rest sq)))))))]
    (bound-seq sq)))

(defn rose-tree
  "Ensure that all Rose tree children have access to the dynamic variables
   currently defined."
  [^RoseTree tree]
  (rose/make-rose
    (rose/root tree)
    (bound-lazy-seq
      (map rose-tree (rose/children tree)))))
