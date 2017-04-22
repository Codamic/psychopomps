(ns psychopomps.jobs.convertors.html
  "This namespace contains all the convertors from html to other formats"
  (:require [psychopomps.unit :as unit]
            [psychopomps.logger :as logger]))

(defrecord HTML->MD []
  unit/Structure
  (start [this]
    (logger/info ">>>> %s" (:inputs this))
    this)
  (stop [this]
    this))


(defn new-html->md-job
  []
  (->HTML->MD))
