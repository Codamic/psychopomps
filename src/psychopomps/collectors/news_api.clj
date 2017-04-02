(ns psychopomps.collectors.news-api
  "`newapi.org` collector namespace."
  (:require [clojure.core.async    :as async]
            [clj-http.client       :as client]
            [environ.core          :refer [env]]
            [psychopomps.logger    :as logger]))


;;TODO: Add the category support for newsapi sources endpoint
(def SOURCES  "https://newsapi.org/v1/sources")
(def ARTICLES "https://newsapi.org/v1/articles")


(defn- fetch-data
  "A helper to fetch data from `newapi.org`."
  [url data success-channel]
  (logger/debug "Fetching data from '%s'" url)
  (client/get url
              {:async? true
               :throw-entire-message? true
               :accept :json
               :headers {"X-Api-Key" (env :news-api-key)}
               :query-params data}
              ;; respond callback
              (fn [response]
                (logger/info response)
                (async/put! success-channel response))
              ;; raise callback
              (fn [exception]
                (logger/error "Request failed with %s" (:status (.getData exception)))
                (logger/error "Server respond with %s" (:body (.getData exception))))))

(defn- fetch-sources
  "Fetch the current sources of `newsapi.org`."
  []
  (let [sources-chan (async/chan 100)]
    (fetch-data SOURCES {:language "en"} sources-chan)
    sources-chan))

(defn- fetch-article-from-source
  [sources-chan articles-chan]
  (async/go-loop []
    (let [source (async/<! sources-chan)]
      (fetch-data ARTICLES {:source source} articles-chan)
      (recur))))

(defn collector
  "Collect news from `newsapi.org` and write them into the given queue."
  [channel]
  (println "Fetching data from 'newsapi.org'...")
  (let [sources (fetch-sources)]
    (fetch-article-from-source sources channel)))
