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

(defn part1 [input]
  (let [lines (str/split-lines input)]
    (*
     (count (filter (has-n-of-char 2) lines))
     (count (filter (has-n-of-char 3) lines)))))

(println (part1 (slurp "input.txt")))
