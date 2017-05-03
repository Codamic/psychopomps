(ns psychopomps.messaging.articles
  "Message handlers for article resource"
  (:require [hellhound.components.core   :as component]
            [hellhound.messaging.helpers :refer [update-app-db]]))

(defn fetch-latest
  "fetch the latest articles and send them to the client who asked for it"
  [params send-fn ev-msg]
  (update-app-db (:uid ev-msg) [:articles] [{:title "sameer"}]))
