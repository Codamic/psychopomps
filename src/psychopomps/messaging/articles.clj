(ns psychopomps.messaging.articles
  (:require [hellhound.components.core :as component]))

(defn fetch-latest
  "fetch the latest articles and send them to the client who asked for it"
  [params uid send-fn ev-msg]
  (send-fn  uid [:some/event {:name "asdad"}]))
