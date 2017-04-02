(ns psychopomps.core
  "Psychopomps is the godess who guide the souls to underworld."
  (:require [clojure.core.async          :as async]
            [psychopomps.collectors.core :refer [collect-news]]
            [psychopomps.logger          :as logger])
  (:gen-class))

(defn end-of-pipeline
  "This is a function for debug purposes"
  [chan]
  (async/go-loop [article (async/<! chan)]
    (println (format "Article: %s" article))
    (recur (async/<! chan))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Starting Psychopomps...")
  (logger/stdout-logger)
  (-> (collect-news)
      (end-of-pipeline)))
