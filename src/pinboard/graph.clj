;; graph.clj
;; AndrewJ 2019-06-07
;; Generic graph and GraphML functions

(ns pinboard.graph
  (:gen-class)
  (:require [ubergraph.core :as uber]
            [clojure.data.xml :as xml]))

(defn all-degrees
  "Return the degrees of all the nodes as a map."
  [g]
  (zipmap (uber/nodes g)
          (map (partial uber/in-degree g)
               (uber/nodes g))))

;; My Pinboard attributes
(def pinboard-attributes
    [(xml/element :key {:id "d0" :for "node" :attr.name "href" :attr.type "string"})
     (xml/element :key {:id "d1" :for "node" :attr.name "description" :attr.type "string"})
     (xml/element :key {:id "d2" :for "node" :attr.name "time" :attr.type "string"})
     (xml/element :key {:id "d3" :for "node" :attr.name "type" :attr.type "string"})])

(defn node-data
  "Convert a graph node to GraphML."
  [g n]
  (let [attrs (uber/attrs g n)]
    (if (empty? attrs)
      (xml/element :node {:id (name n)})
      ;; else
      (xml/element :node {:id (name n)}
                   (xml/element :data {:key "d0"} (:href attrs))
                   (xml/element :data {:key "d1"} (:description attrs))
                   (xml/element :data {:key "d2"} (:time attrs))
                   (xml/element :data {:key "d3"} (:type attrs))))))

(defn edge-data
  "Convert a graph edge to GraphML."
  [_ e]
  (xml/element :edge {:source (name (:src e)) :target (name (:dest e))}))

(defn- edge-default
  [g]
  (if (uber/undirected-graph? g)
    "undirected"
    ;; else
    "directed"))

(defn to-graphml
  "Convert Ubergraph to a GraphML format using Pinboard attributes."
  [g]
  (xml/element :graphml
               {:xmlns "http://graphml.graphdrawing.org/xmlns"}
               (xml/element :graph
                            {:id "G" :edgedefault (edge-default g)}
                            pinboard-attributes
                            (map (partial node-data g) (uber/nodes g))
                            (map (partial edge-data g) (uber/edges g)))))

(defn write-graphml
  "Write XML tree to a file."
  [x out-file-name]
  (with-open [out (java.io.FileWriter. out-file-name)]
    (xml/emit x out)))

;; The End
