(ns psychopomps.collectors.component
  "The collector component namespace which contains the actual
  collectors component to be used with system maps."
  (:require [com.stuartsierra.component  :as component]
            [psychopomps.logger          :as logger]
            [psychopomps.collectors.core :refer [collect-news]]
            [clojure.core.async :as async]))


(defrecord CollectorPool []
  component/Lifecycle
  (start [this]
    (logger/info "Starting the collectos pool...")
    (assoc this :output-channel (collect-news)))

  (stop [this]
    (if (:output-channel this)
      (do
        (logger/info "Tearing down the collectos pool...")
        (async/close! (:output-channel this))
        (dissoc this :output-channel))
      this)))


(defn new-collector-pool
  "Create and instance of `CollectorPool` component."
  []
  (->CollectorPool))
