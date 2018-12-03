(ns day3
  (:require [clojure.string :as str]))

(defn get-lines [] (str/split-lines (slurp "input.txt")))

(defn convert-line-to-claim [line]
  (let [[id left top width height]
        (->>
         (re-matches #"#(\d+) @ (\d+),(\d+): (\d+)x(\d+)" line)
         (rest)
         (map #(Integer/parseInt %)))]
    {:id id, :left left, :top top, :width width, :height height}))

(defn convert-claim-to-squares [claim]
  (let [left (:left claim), top (:top claim), width (:width claim) height (:height claim)]
    (for [x (range left (+ left width))
          y (range top (+ top height))]
      (list x y))))

(defn get-num-overlapping-squares [claims]
  (->>
   claims
   (mapcat convert-claim-to-squares)
   (frequencies)
   (filter #(> (second %) 1))
   (count)))

(defn get-claims [] (map convert-line-to-claim (get-lines)))

(defn part1 [] (get-num-overlapping-squares (get-claims)))

(defn get-squares-by-id [claims]
  (->>
   claims
   (mapcat #(list (:id %) (convert-claim-to-squares %)))
   (apply hash-map)))

(defn get-freqs [squares-by-id]
  (->>
   (vals squares-by-id)
   (apply concat)
   (frequencies)))

(defn part2 []
  (let
   [squares-by-id (get-squares-by-id (get-claims))
    freqs (get-freqs squares-by-id)
    does-not-overlap (fn [[id, squares]]
                       (every?
                        #(= 1 (freqs %))
                        squares))]
    (->>
     squares-by-id
     (filter does-not-overlap)
     (map first))))

(println (part2))