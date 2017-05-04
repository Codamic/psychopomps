(ns psychopomps.messaging.articles
  "Message handlers for article resource"
  (:require [hellhound.components.core   :as component]
            [hellhound.messaging.helpers :refer [append->app-db]]
            [psychopomps.cache.core      :refer [batch<-cache]]))

(defn fetch-latest
  "fetch the latest articles and send them to the client who asked for it"
  [params send-fn ev-msg]
  (let [recent   (apply batch<-cache :articles params)
        articles (map-indexed #(assoc (dissoc %2 :raw-content)
                                      ;; TODO: Get ride of the random number
                                      :id (rand-int 1000000)) recent)]
    (append->app-db (:uid ev-msg) [:recent-articles] articles)))
