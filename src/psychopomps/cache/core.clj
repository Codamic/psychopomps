(ns psychopomps.cache.core
  "This namespace provides several functions to cache data inside redis"
  (:require [environ.core     :refer [env]]
            [taoensso.carmine :as car :refer [wcar]]))

(def redis-config {:pool {} :spec {:uri (env :redis-spec)}})

(defmacro redis->
  "Use the connection pool to redis in order to run the given
  commands in the `redis` cluster."
  [& body]
  `(wcar redis-config ~@body))


(defn ->cache
  "Store the given `data` with the given `key` into the cache"
  [key data]
   (redis->
           (car/set (name key) data)))

(defn <-cache
  "Fetch the data for the given key from the cache and return it"
  [key]
  (redis->
   (car/get (name key))))

(defn push->cache
  "Push the given `key` to the array specified by the given key `name`."
  [key value]
  (redis->
   (car/lpush (name key) value)))

(defn pop<-cache
  "Pull the last value in the array with the given `key`"
  [key]
  (redis->
   (car/lpop (name key))))
