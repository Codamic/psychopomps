(ns psychopomps.core
  "Psychopomps is the godess who guide the souls to underworld."
  (:require [clojure.core.async          :as async]
            [psychopomps.logger          :as logger]
            [psychopomps.utils           :refer [while-let]])
  (:gen-class))

(defn end-of-pipeline
  "This is a function for debug purposes"
  [chan]
  ;(async/go)
  (while-let [article (async/<!! chan)]
    (logger/info "Article: %s ||| %s" (:raw-content article) (:url article))))

(.addShutdownHook
 (Runtime/getRuntime)
 (Thread. (fn []
            ;;(logger/info "Shutting down...")
            (println "Shutting down...")
            (stop))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; (let [news-channel (collect-news)]
  ;;   (-> news-channel
  ;;       (end-of-pipeline)))
)
