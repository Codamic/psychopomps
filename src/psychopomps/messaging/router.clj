(ns psychopomps.messaging.router
  (:require [hellhound.messaging            :refer [defrouter]]
            [psychopomps.messaging.articles :as articles]))

(defrouter message-router
  {:fetch-articles articles/fetch-latest})
