(ns psychopomps.messaging.router
  "Message router namespace"
  (:require [hellhound.messaging.core       :refer [defrouter]]
            [psychopomps.messaging.articles :as articles]))

(defrouter message-router
  {:fetch-articles articles/fetch-latest})
