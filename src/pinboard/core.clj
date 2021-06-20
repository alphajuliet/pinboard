(ns pinboard.core
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [pinboard.graph :as gr]
            [ubergraph.core :as uber])
  (:gen-class))

(def testf "data/test.json")
(def inputf "data/pinboard-2021-06-20.json")

(defn read-export
  "Read the exported data into a map, and separate tags into a vector."
  [f]
  (as-> f <>
    (slurp <>)
    (json/parse-string <> true)
    (map (fn [e] (update e :tags #(str/split % #"\s+"))) <>)))

(defn add-bookmark
  "Add a edge between the bookmark and each tag,
   and attach some attributes to the bookmark."
  [graph bmark]
  (let [id (:hash bmark)]
    (reduce (fn [g tag]
              (if (empty? tag)
                g
                ;; else
                (let [tag-id (keyword tag)]
                  (-> g
                      (uber/add-edges [id tag-id])
                      (uber/add-attrs id (into {:type "node"}
                                               (select-keys bmark [:href :description :time])))
                      (uber/add-attrs tag-id {:type "tag" :description tag})))))
            graph
            (:tags bmark))))

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
