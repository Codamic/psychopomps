(ns psychopomps.collectors.core
  "Collector core is responsible for running all collectores and save
  their results."
  (:require [clojure.core.async              :refer [chan]]
            [psychopomps.collectors.news-api :as news-api]))


(defn collect-news
  "Fire up collectores to collect news and put them in the queue"
  []
  (news-api/collector))
