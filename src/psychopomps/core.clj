(ns psychopomps.core
  "Psychopomps is the godess who guide the souls to underworld."
  (:require [clojure.core.async          :as async]
            [psychopomps.collectors.core :refer [collect-news]]
            [psychopomps.logger          :as logger]
            [psychopomps.utils           :refer [while-let]])
  (:gen-class))

(defn end-of-pipeline
  "This is a function for debug purposes"
  [chan]
  (while-let [article (async/<!! chan)]
    (logger/info "Article: %s" article)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Starting Psychopomps...")
  (logger/stdout-logger)
  (-> (collect-news)
      (end-of-pipeline)))
