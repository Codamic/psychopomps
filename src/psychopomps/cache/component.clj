(ns psychopomps.cache.component
  "The caching component to cache the fetched articles inside redis"
  (:require [hellhound.components.protocols  :as protocols]
            [psychopomps.cache.core          :refer [push->cache]]
            [clojure.core.async              :as async]
            [clojure.core.async              :as async]
            [psychopomps.utils               :refer [while-let]]
            [hellhound.logger.core           :as logger]))



(defrecord RedisCache []
  protocols/Lifecycle
  (start [this]
    (let [input-chan (first (:inputs this))]
      (assoc this
             :output
             (async/go (while-let [article (async/<! input-chan)]
                         (logger/info "Caching article '%s'" (:title article))
                         (if-not (empty? article)
                           (push->cache :articles article)))))))
  (stop [this]
    (if-not (nil? (:output this))
      (do
        (async/close! (:output this))
        (dissoc this :output)))))


(defn new-redis-cache
  []
  (->RedisCache))
