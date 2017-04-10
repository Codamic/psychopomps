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
  ;(async/go)
  (while-let [article (async/<!! chan)]
    (logger/info "Article: %s ||| %s" (:id (:source article)) (count (:articles article)))))

(.addShutdownHook
 (Runtime/getRuntime)
 (Thread. (fn []
            (logger/info "Shutting down...")
            (logger/stop))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Starting Psychopomps...")
  (logger/stdout-logger)
  (let [news-channel (collect-news)]
    (-> news-channel
        (end-of-pipeline))))
