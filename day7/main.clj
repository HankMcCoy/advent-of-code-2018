(ns day7
  (:require [clojure.string :as str])
  (:require [clojure.set :as set]))

(defn get-lines [] (str/split-lines (slurp "input.txt")))

(defn parse-line [line] (hash-map :prev (nth line 5), :next (nth line 36)))

(defn char-range [start end]
  (map char (range (int start) (inc (int end)))))

(defn get-rules [lines] (map parse-line lines))

(defn get-rules-map [rules]
  (reduce
   (fn [rules-map rule]
     (update rules-map (:next rule) #(conj (or % #{}) (:prev rule))))
   {}
   rules))

(defn get-steps-without-prereqs
  [steps rules]
  (filter #() steps))

(defn get-unblocked-steps [rules finished-steps]
  (->> rules
       (filter (fn [[_ prereqs]] (every? finished-steps prereqs)))
       (map (fn [[step]] step))
       (filter #(not (finished-steps %)))))

(defn part1 []
  (let [steps (char-range \A \Z)
        rules (get-rules-map (get-rules (get-lines)))
        initially-unblocked-steps (filter #(not (contains? rules %)) steps)]
    (apply
     str
     (reverse
      (loop [ordered-steps '()
             finished-steps #{}
             [cur-step & unblocked-steps] initially-unblocked-steps]
        (if
         (nil? cur-step)
          ordered-steps
          (let [next-finished-steps (conj finished-steps cur-step)
                next-unblocked-steps (apply sorted-set
                                            (set/union
                                             unblocked-steps
                                             (get-unblocked-steps rules next-finished-steps)))]
            (recur
             (cons cur-step ordered-steps)
             next-finished-steps
             next-unblocked-steps))))))))
(println (part1))
