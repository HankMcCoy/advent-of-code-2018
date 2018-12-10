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
       (filter #(not ((set finished-steps) %)))))

(defn render-result [step-list] (println (apply str (reverse step-list))))
(defn part1 []
  (let [steps (char-range \A \Z)
        rules (get-rules-map (get-rules (get-lines)))
        initially-unblocked-steps (filter #(not (contains? rules %)) steps)]
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
           next-unblocked-steps))))))
(render-result (part1))

(defn get-countdown [step] (- (int step) 4))
(defn part-by [predicate coll] ((juxt filter remove) predicate coll))
(defn advance-time [workers]
  (map #(update % :countdown dec) workers))

(defmacro with-log
  "Log a statement, then evaluate the expression"
  [log expr]
  `(do (println ~log) ~expr))

(defn part2 []
  (let [steps (char-range \A \Z)
        rules (get-rules-map (get-rules (get-lines)))
        initially-unblocked-steps (filter #(not (contains? rules %)) steps)]
    (loop [workers []
           finished-steps []
           unblocked-steps initially-unblocked-steps
           time 0]
      (if
       (and (empty? unblocked-steps) (empty? workers))
        (dec time)
        (let [[finished-workers active-workers] (part-by #(= 0 (:countdown %)) workers)
              new-finished-steps (map #(:step %) finished-workers)
              next-finished-steps (concat finished-steps new-finished-steps)
              active-steps (map #(:step %) active-workers)
              new-unblocked-steps (filter #(nil? ((set active-steps) %)) (get-unblocked-steps rules (set next-finished-steps)))
              cur-unblocked-steps (apply sorted-set (set/union unblocked-steps new-unblocked-steps))
              num-free-workers (- 5 (count active-workers))
              steps-to-work-on (take num-free-workers cur-unblocked-steps)
              next-unblocked-steps (drop num-free-workers cur-unblocked-steps)
              new-workers (map #(hash-map :step % :countdown (get-countdown %)) steps-to-work-on)]
          (recur
           (advance-time (concat active-workers new-workers))
           next-finished-steps
           next-unblocked-steps
           (inc time)))))))
(println (part2))
