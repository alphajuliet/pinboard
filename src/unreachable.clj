#!/usr/bin/env bb

(ns unreachable
  (:require [babashka.http-client :as http]
            [cheshire.core :as json]))

(defn read-pinboard
  "Import the JSON backup file"
  [f]
  (let [contents (slurp f)]
    (json/parse-string contents true)))

(defn write-log
  "Write the data to a designated log file"
  [data]
  (let [output-file "data/unreachable-links.json"]
    (spit output-file 
          (json/generate-string data {:pretty true}) 
          :append true)))

(defn check-uri
  "See if the URI is alive"
  [uri]
  (let [{:keys [status]} (try
                           (http/head uri {:throw false
                                           :connect-timeout 3000 ; ms
                                           :client (http/client {:follow-redirects :never})})
                           (catch Exception _ {:status "ERR" :uri uri}))]
    {:status status :uri uri}))

(defn check-all-links
  "Check reachability of each bookmark"
  [bookmarks]
  (for [b bookmarks
        :let [uri (:href b)]]
    (check-uri uri)))

(defn -main
  [& args]
  (let [data (read-pinboard "data/pinboard-2024-01-07.json")]
    #_(println (str "Links:" (count data)))
    (->> data
         (drop 400)
         (take 200)
         check-all-links
         write-log)))