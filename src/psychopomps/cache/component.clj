(ns psychopomps.cache.component
  "The caching component to cache the fetched articles inside redis"
  (:require [hellhound.components.core :as component]
            [psychopomps.cache.core    :refer [push->cache]]
            [clojure.core.async        :refer [take!]]))



(defrecord RedisCache []
  component/Lifecycle
  (start [this])
  (stop [this]))
