(ns psychopomps.jobs.convertors.html
  "This namespace contains all the convertors from html to other formats"
  (:require [psychopomps.unit :as unit]))

(defrecord HTML->MD []
  unit/Structure
  (start [this])
  (stop [this]))


(defn new-html->md-job
  []
  (->HTML->MD))
