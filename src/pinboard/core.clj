(ns pinboard.core
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [pinboard.graph :as gr]
            [ubergraph.core :as uber])
  (:gen-class))

;; ----------------
(defn read-export
  "Read the exported data into a map, and separate tags into a vector."
  [f]
  (as-> f <>
    (slurp <>)
    (json/parse-string <> true)
    (map (fn [e] (update e :tags #(str/split % #"\s+"))) <>)))

;; ----------------
(defn all-tags
  "Produce a lazy sequence of all tags from the bookmark data."
  [coll]
  (->> coll
       (map :tags)
       flatten
       sort
       dedupe
       (filter #(> (count %) 0))))

(defn bookmarks-with-tag
  "Return those bookmarks with a given tag."
  [coll tag]
  (filter #((set (:tags %)) tag) coll))

(defn coincident-tags
  "List all the tags that are coincident with the given tag."
  [coll tag]
  (all-tags (bookmarks-with-tag coll tag)))

;; ----------------
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

;; ----------------
(defn add-tag-node
  "Add a tag node to the graph with edges to all coincident tags."
  [graph coll tag]
  (let [dests (coincident-tags coll tag)]
    (reduce (fn [g dest]
              (if (not= tag dest)
                (uber/add-edges g [(keyword tag) (keyword dest)])
                g))
            graph
            dests)))

;; ----------------
(defn create-graph
  "Create the graph from a collection of maps of entries."
  [m]
  (reduce add-bookmark (uber/graph) m))

;; ----------------
(defn create-tag-graph
  "Derive a graph of just tags."
  [coll]
  (let [tags (all-tags coll)]
    (reduce (fn [g tag]
              (add-tag-node g coll tag))
            (uber/graph)
            tags)))

;; ----------------
(defn make-full-graph
  "Run the pipeline to make the full graph, from JSON to GraphML."
  [infile outfile]
  (-> infile
      read-export
      create-graph
      (gr/pinboard-to-graphml)
      (gr/write-graphml outfile)))

(defn make-tag-graph
  "Make a tag-only graph."
  [infile outfile]
  (-> infile
      read-export
      create-tag-graph
      gr/tags-to-graphml
      (gr/write-graphml outfile)))

;; The End
