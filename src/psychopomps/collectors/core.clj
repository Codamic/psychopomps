(ns psychopomps.collectors.core
  "Collector core is responsible for running all collectores and save
  their results."
  (:require [clojure.core.async              :refer [chan]]
            [psychopomps.collectors.news-api :as news-api]))


(def news-channel  (chan 1000))


(defn collect-news
  "Fire up collectores to collect news and put them in the queue"
  []
  (-> news-channel
      (news-api/collector))
  news-channel)
