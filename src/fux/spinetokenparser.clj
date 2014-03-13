(ns fux.spinetokenparser)

(def note-map {
               "." 0 nil 0
               "" 0
              "CC" 36
              "CC#" 37
              "DD-" 37
              "DD" 38
              "DD#" 39
              "EE-" 39
              "EE" 40
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
  (if (= \= (first token))
    nil
    (do
      (clojure.string/join (re-seq #"[0-9.]" token)))))

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
