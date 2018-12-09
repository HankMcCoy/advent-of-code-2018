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

(defn get-bounds [coords]
  (let [coords-by-x (sort-by :x coords)
        coords-by-y (sort-by :y coords)]
    (list (point (:x (first coords-by-x)) (:y (first coords-by-y)))
          (point (:x (last coords-by-x)) (:y (last coords-by-y))))))

(defn get-area-size [cur-coord coords bounds])

(defn is-on-edge
  [{x :x, y :y} [{first-x :x, first-y :y} {last-x :x, last-y :y}]]
  (or (= x first-x) (= x last-x) (= y first-y) (= y last-y)))

(defn get-grid-coords [coords bounds]
  (let [[{first-x :x, first-y :y} {last-x :x, last-y :y}] bounds]
    (for [y (range first-y (inc last-y))
          x (range first-x (inc last-x))]
      {:x x, :y y})))

(defn get-loc-counts [locs]
  (let
   [bounds (get-bounds locs)
    grid-points (get-grid-coords locs bounds)]
    (loop [[cur-point & rest] grid-points
           bad-locs #{}
           loc-counts {}]
      (if
       (nil? cur-point)
        loc-counts
        (let [nearest-loc (get-closest cur-point locs)]
          (cond
            (nil? nearest-loc) (recur rest bad-locs loc-counts)
            (is-on-edge cur-point bounds) (recur rest (conj bad-locs nearest-loc) loc-counts)
            :else (recur rest bad-locs (update loc-counts nearest-loc #(+ 1 (or % 0))))))))))

(defn part1 []
  (->>
   (get-loc-counts (get-coords))
   (sort-by second)
   (reverse)
   (first)))

(defn get-total-manhattan
  [point locs]
  (reduce
   (fn [dist loc] (+ dist (get-manhattan point loc)))
   0
   locs))

(defn part2 []
  (let
   [locs (get-coords)
    bounds (get-bounds locs)
    grid-points (get-grid-coords locs bounds)]
    (->>
     grid-points
     (map #(hash-map :point %, :dist (get-total-manhattan % locs)))
     (filter #(< (:dist %) 10000))
     (count))))

(defn print-board []
  (let [coords (get-coords)
        bounds (get-bounds coords)
        [_ {last-x :x}] bounds
        grid-coords (get-grid-coords coords bounds)]
    (apply str
           (for [{x :x, y :y} grid-coords]
             (let [px (if (some #(= (point x y) %) coords) "X" ".")]
               (if (= x last-x) (str px "\n") px))))))

(println "Part 1:")
(println (part1))

(println "Part 2:")
(println (part2))
