(ns pinboard.core
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [pinboard.graph :as gr]
            [ubergraph.core :as uber])
  (:gen-class))

(def testf "data/test.json")
(def inputf "data/pinboard-2021-03-07.json")

(defn read-export
  "Read the exported data into a map, and separate tags into a vector."
  [f]
  (as-> f <>
    (slurp <>)
    (json/parse-string <> true)
    (map (fn [e] (update e :tags #(str/split % #"\s+"))) <>)))

(defn add-bookmark
  "Add a bookmark map to the graph."
  [g bmark]
  (let [id (:hash bmark)]
    (reduce (fn [g1 tag]
              (if (empty? tag)
                g1
                ;; else
                (-> g1
                   (uber/add-edges [id (keyword tag)])
                   (uber/add-attrs id (select-keys bmark [:href :description :time])))))
            g (:tags bmark))))

(defn create-graph
  "Create the graph from a map of entries."
  [m]
  (reduce add-bookmark (uber/graph) m))

(defn go
  "Run the pipeline from JSON to GraphML."
  [infile outfile]
  (-> infile
      read-export
      create-graph
      gr/to-graphml
      (gr/write-graphml outfile)))

;; The End
