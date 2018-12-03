(ns day2
  (:require [clojure.string :as str]))

(defn get-count-map [word]
  (loop [cur-segment word
         counts {}]
    (if (first cur-segment)
      (recur
       (rest cur-segment)
       (update counts
               (first cur-segment)
               #(if (nil? %) 1 (+ % 1))))
      counts)))

(defn has-n-of-char [n]
  (fn [word]
    (> (count (filter
               (comp #{n} last)
               (get-count-map word)))
       0)))

(defn part1 [lines]
  (*
   (count (filter (has-n-of-char 2) lines))
   (count (filter (has-n-of-char 3) lines))))

(defn get-common-letters
  [init-a init-b]
  (loop [a init-a
         b init-b
         common-letters ""]
    (cond
      (or (empty? a) (empty? b)) common-letters
      (= (first a) (first b)) (recur (subs a 1) (subs b 1) (str common-letters (first a)))
      :else (recur (subs a 1)  (subs b 1) common-letters))))

(defn part2 [ids]
  (let [[a b]
        (first (for [a ids
                     b ids
                     :while (not= a b)
                     :when (= (- (count a) 1) (count (get-common-letters a b)))]
                 (list a b)))]
    (get-common-letters a b)))

(defn get-lines [] (str/split-lines (slurp "./input.txt")))

(println (part1 (get-lines)))
(println (part2 (get-lines)))
