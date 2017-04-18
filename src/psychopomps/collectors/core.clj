(ns psychopomps.collectors.core
  "Collector core is responsible for running all collectores and save
  their results."
  (:require [clojure.core.async              :refer [chan close!]]
            [psychopomps.collectors.news-api :as news-api]
            [psychopomps.logger              :as logger]
            [psychopomps.unit                :as unit]))

(defn collect-news
  "Fire up collectores to collect news and put them in the queue"
  []
  (news-api/collector))


(defrecord CollectorsPool []
  unit/Structure
  (start [this]
    (logger/info "Starting the collectors pool...")
    (assoc this :output-chan (collect-news)))
  (stop [this]
    (if (:output-chan this)
      (let [channel (:output-chan this)]
        (close! channel)
        (dissoc this :output-chan))
      this)))

(defn new-collector-pool
  "Create a new collectors pool"
  []
  (->CollectorsPool))
