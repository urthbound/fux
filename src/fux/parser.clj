(ns fux.parser
    (:require [clojure.java.io :as io]))

(defn prep-kern [input-path]
  (with-open [rdr (io/reader input-path)]
    (->> (line-seq rdr)
         (into [] ))))

; ==============
; comments extractor
; ==============

(defn extract-comments [kern]
  (remove #(not= \! (first %)) kern))

; ==============
; spines extractor
; ==============

(defn strip-comments [kern]
  (remove #(= \! (first %)) kern))

(defn split-spines [kern]
  (let [vectors (strip-comments kern)]
    (map #(clojure.string/split % #"\t") vectors)))

(defn tokenize-spine [spines]
    (for [ i spines ]
      (first i)))

(defn extract-spines [spines]
; (list (remove #(= "." %) (flatten
  (loop [spines (split-spines spines)
         acc (vector)]
      (if (empty? (flatten spines))
        acc
        ; (conj (tokenize-spine spines) acc))))
        (recur (map rest spines) (conj acc (tokenize-spine spines))))))
; ))

; ==============
; spines parser
; ==============

(defn extract-spine-comments [spine]
  (remove #(not= \* (first %)) spine))

(defn strip-spine-comments [spine]
  (remove #(= \* (first %)) spine))

(defn remove-null [spine]
  (remove #(= \. (first %)) spine))

(defn remove-measure [spine]
  (remove #(= \= (first %)) spine))

(def note-map {
               "." 0 
               nil 0
               "" 0
               "r" 0
              "CC" 36
              "CC#" 37
              "DD-" 37
              "DD" 38
              "DD#" 39
              "EE-" 39
              "EE" 40
              "EE#" 41
              "FF" 41
              "FF#" 42
              "GG-" 42
              "GG" 43
              "GG#" 44
              "AA-" 44
              "AA" 45
              "AA#" 46
              "BB-" 46
              "BB" 47
              "C" 48
              "C#" 49
              "D-" 49
              "D" 50
              "D#" 51
              "E-" 51
              "E#" 52
              "E" 52
              "F" 53
              "F#" 54
              "G-" 54
              "G" 55
              "G#" 56
              "A-" 56
              "A" 57
              "A#" 58
              "B-" 58
              "B" 59
              "c" 60
              "c#" 61
              "d-" 61
              "d" 62
              "d#" 63
              "e-" 63
              "e" 64
              "e#" 65
              "f" 65
              "f#" 66
              "g-" 66
              "g" 67
              "g#" 68
              "a-" 68
              "a" 69
              "a#" 70
              "b-" 70
              "b" 71
              "cc" 72
              "cc#" 73
              "dd-" 73
              "dd" 74
              "dd#" 75
              "ee-" 75
              "ee" 76
              "ee#" 77
              "ff" 77
              "ff#" 78
              "gg-" 78
              "gg" 79
              "gg#" 80
              "aa-" 80
              "aa" 81
              "aa#" 82
              "bb-" 82
              "bb" 83
              "ccc" 83})

(defn duration [token]
  (let [duration (clojure.string/join (re-seq #"[0-9.]" token))]
    (if (= \. (last duration))
      (* 6 (/ 1 (read-string duration)))
      (* 4 (/ 1 (float (read-string duration)))))))

(defn add-offset [spine]
    (rest (reverse (reduce (fn [acc note]
       (conj acc
         (assoc note
            :offset (+ ((first acc) :duration)
                       ((first acc) :offset)))))
        '({:offset 0 :duration 0})
        spine))))


(defn note [token]
  (if (= \= (first token))
    nil
    (do
      (clojure.string/join (re-seq #"[A-Ga-g#-]" token)))))


(defn spine-noter [tokens]
   (into []
     (for [token tokens]
       (if (not= \= (first token))
         {:duration (duration token)
          :note (note token)
          :notecode (note-map (note token))}
         token))))

(defn chunk-spine [spine]
  {:comments (extract-spine-comments spine)
   :notes (spine-noter (remove-measure (remove-null (strip-spine-comments spine))))})

(defn chunk-all-spines [kern]
  (loop [spines (extract-spines kern)
         acc [] ]
    (if (empty? spines)
      acc
      (recur (rest spines) (conj acc (chunk-spine (first spines)))))))


; ==============
; kern parser
; ==============

(defn parse-kern [kern]
  {:global-comments (extract-comments kern)
   :spines (map #(add-offset (% :notes))(map chunk-spine (extract-spines kern)))})


