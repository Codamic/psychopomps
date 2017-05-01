(ns psychopomps.jobs.convertors.html
  "This namespace contains all the convertors from html to other formats"
  (:require [hellhound.components.core :as component]
            [hellhound.logger.core        :as logger]
            [psychopomps.utils         :refer [while-let]]
            [clojure.core.async        :as async]
            [environ.core              :refer [env]]))

(defn convert->md
  [article]
  (logger/info (:url article)))

(defn convert
  [input-chan]
  (let [out (async/chan 5000)]
    (async/pipeline (env :pool-size) out (map convert->md) input-chan)
    out))

(defrecord HTML->MD []
  component/Lifecycle
  (start [this]
    (logger/info "Starting HTML convertor...")
    (let [[input] (:inputs this)]
      )
    this)
  (stop [this]
    this))


(defn new-html->md-job
  []
  (->HTML->MD))
