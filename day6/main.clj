(ns day3
  (:require [clojure.string :as str]))

(defn get-lines [] (str/split-lines (slurp "input.txt")))

(defn point [x y] {:x x, :y y})
(defn parse-coord [line]
  (let [[x y] (->>
               (re-matches #"(-?\d+), (-?\d+)" line)
               (rest)
               (map #(Integer/parseInt %))
               (apply vector))]
    (point x y)))

(defn get-coords [] (apply vector (map parse-coord (get-lines))))

(defn get-manhattan [c1 c2]
  (+
   (Math/abs (- (:x c1) (:x c2)))
   (Math/abs (- (:y c1) (:y c2)))))

(defn get-closest [cur-point coords]
  (let [by-dist (->>
                 coords
                 (map-indexed
                  #(hash-map :idx %1, :dist (get-manhattan cur-point %2)))
                 (sort-by :dist))]
    (if (= (first by-dist) (second by-dist))
      nil
      (nth coords (:idx (first by-dist))))))

;(defn get-bound [get-coord-part compare coords] )
(defn get-bounds [coords]
  (let [coords-by-x (sort-by :x coords)
        coords-by-y (sort-by :y coords)]
    (list (point (:x (first coords-by-x)) (:y (first coords-by-y)))
          (point (:x (last coords-by-x)) (:y (last coords-by-y))))))

(defn get-area-size [cur-coord coords bounds])

(defn part1 []
  (let
   [coords (get-coords)
    bounds (get-bounds coords)]
    bounds))

(defn print-board []
  (println "TEST")
  (let [coords (get-coords)
        bounds (get-bounds coords)
        first-x (:x (first bounds))
        first-y (:y (first bounds))
        last-x (:x (last bounds))
        last-y (:y (last bounds))]
    (apply str
           (for [y (range first-y (inc last-y))
                 x (range first-x (inc last-x))]
             (let [px (if (some #(= (point x y) %) coords) "X" ".")]
               (if (= x last-x) (str px "\n") px))))))

;(println (contains? (get-coords) (point 279 84)))
(println (print-board))
;(println (part1))
;(defn part1 [] (get-coords))
;(defn part2 [] "fail")

;(println (part1))
;(println (part2))
