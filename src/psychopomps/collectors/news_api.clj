(ns psychopomps.collectors.news-api
  "`newapi.org` collector namespace."
  (:require [clojure.core.async    :refer [go <! >! <!! go-loop chan
                                           close! sliding-buffer put!]
                                   :as async]
            [org.httpkit.client    :as client]
            [environ.core          :refer [env]]
            [cheshire.core         :refer [parse-string]]
            [psychopomps.logger    :as logger]
            [psychopomps.utils     :refer [while-let fetch> fetch]]))


;;TODO: Add the category support for newsapi sources endpoint
(def SOURCES  "https://newsapi.org/v1/sources")
(def ARTICLES "https://newsapi.org/v1/articles")

(defn- query-options
  [data]
  {:timeout 2000
   :query-params data
   :headers {"X-Api-Key" (env :news-api-key)}})

(defn- fetch-sources
  "Fetch the current sources of `newsapi.org`."
  []
  (fetch SOURCES
         (query-options {:language "en"})
         false))

(defn- parse-sources
  "Parse the json  structure inside the `in` channel and return a vector of
  channels containing the sources."
  [data]
  (logger/debug "Parsing sources...")
  (let [parsed-data (parse-string data true)
        sources     (:sources parsed-data)
        out         (chan 100)]
    (async/onto-chan out sources)
    out))

(defn- extract-articles-from-body
  "Extract the articles from the the API response."
  [response source]
  (let [articles (:articles response)]
    (map #(assoc % :source source) articles)))

(defn- fetch-articles
  "Fetch all the articles for the given source"
  [source]
  (logger/info "Fetching from %s" (:id source))
  (if-not (nil? source)
    (let [source-id (:id source)
          body      (fetch ARTICLES (query-options {:source source-id}) false)]
      (if-not (empty? body)
        (extract-articles-from-body (parse-string body true)
                                    source)))
    {}))


(defn- fetch-all-articles
  "Fetch all the articles by iterating over given sources and fetch artices
  for each one."
  [source-channels out]
  (async/pipeline-blocking 10 out (comp (map fetch-articles) cat)  source-channels false)
  out)


(defn- fetch-details
  [article]
  (logger/warn "<<< 0000000000000000000 %s" (:url article) )
  (let [body (fetch (:url article) {:timeout 2000} false)]
    (if-not (empty? body)
      (do
        (logger/debug "Fetched %s" (:url article))
        (assoc article :raw-content body))
      {})))

(defn- fetch-article-details
  ""
  [in]
  (let [out (chan 5000)]
    (logger/debug "Fetching details of articles")
    ;; TODO: Change the map to filter?
    (async/pipeline-blocking 10 out (map fetch-details) in false)
    out))

(defn collector
  "Collect news from `newsapi.org` and write them into the given queue."
  []
  (let [articles (chan 5000)
        sources  (parse-sources (fetch-sources))]
    (fetch-article-details (fetch-all-articles sources articles))))
