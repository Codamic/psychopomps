(ns psychopomps.collectors.news-api
  "`newapi.org` collector namespace."
  (:require [clojure.core.async    :refer [go <! >! <!! go-loop chan
                                           close! sliding-buffer]]
            [org.httpkit.client    :as client]
            [environ.core          :refer [env]]
            [cheshire.core         :refer [parse-string]]
            [psychopomps.logger    :as logger]
            [psychopomps.utils     :refer [while-let]]))


;;TODO: Add the category support for newsapi sources endpoint
(def SOURCES  "https://newsapi.org/v1/sources")
(def ARTICLES "https://newsapi.org/v1/articles")

(defn- query-options
  [data]
  {:timeout 2000
   :query-params data
   :headers {"X-Api-Key" (env :news-api-key)}})

(defn- fetch-data
  "A helper to fetch data from `newapi.org`."
  [url data success-channel]
  (logger/debug "Fetching data from '%s' with %s" url data)
  (client/get url (query-options data)
              (fn [{:keys [status headers body error]}] ;; asynchronous response handling
                (if error
                  (do (logger/warn "Request failed to '%s' with '%s' status." url status)
                      (logger/warn "Body: %s" body))
                  (go (>! success-channel (parse-string body true)))))))

(defn- fetch-sources
  "Fetch the current sources of `newsapi.org`."
  []
  (let [sources-chan (chan 10)]
    (fetch-data SOURCES {:language "en"} sources-chan)
    sources-chan))

(defn- parse-sources
  "Parse the json  structure inside the `in` channel and put each source in
  `out` channel."
  [in]
  (logger/debug "Parsing sources...")
  (let [out (chan 200)]
    (while-let [input (<!! in)]
      (let [sources (:sources input)]
        (doseq [source sources]
          (go (>! out source)))
        (close! in)))
    out))

(defn- fetch-article-from-source
  "Read sources from `in` channel and fetch the articles for them and
  put them in `out` channel."
  [in]
  (logger/debug "Fetching articles...")
  (let [out (chan (sliding-buffer 1000))]
    (while-let [source (<!! in)]
      (fetch-data ARTICLES
                  {:source (:id source)}
                  out))
    (close! in)
    out))

(defn- parse-articles
  "Parse the fetched articles from `in` and create records from each and put them
  into `out` channel"
  [in]
  (logger/debug "Parsing articles...")
  (let [out (chan (sliding-buffer 5000))]
    (while-let [data (<!! in)]
      (let [articles (:articles data)
            source   (:source   data)]
        (doseq [article articles]
          (logger/debug "Article title: %s" (:title article))
          (go (>! out article)))
        (close! in)))
    out))

(defn collector
  "Collect news from `newsapi.org` and write them into the given queue."
  []
  (println "Fetching data from 'newsapi.org'...")
  (-> (fetch-sources)
      (parse-sources)
      (fetch-article-from-source)
      (parse-articles)))
