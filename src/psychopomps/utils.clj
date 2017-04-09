(ns psychopomps.utils
  "A namespace for utility functions"
  (:require [clojure.core.async  :refer [put!] :as async]
            [org.httpkit.client  :as client]
            [psychopomps.logger  :as logger]))

(defmacro while-let
  "Repeatedly executes body while test expression is true, evaluating
  the body with binding-form bound to the value of test."
  [[form test] & body]
  `(loop [~form ~test]
     (when ~form
       ~@body
       (recur ~test))))

(defn- internal-fetch
  [url options]
  (let [{:keys [status headers body error] :as resp} @(client/get url options)]
    (if error
      (do (logger/error "Could not fetch the sources.")
          "")
      body)))

(defn fetch
  "Fetch the given url with the given options and return a channel which
  contains the result"
  ([url options]
   (fetch url options true))
  ([url options async?]
   (if async?
     (async/thread
       (internal-fetch url options))
     (internal-fetch url options))))

(defn fetch>
  "Fetch the given url data and and put it into `out` channel. Any given
  options will pass to `org.httpkit.client/get` function and any given
  function as last parameter will applied to the result which is going
  to go in `out` channel."
  ([out url]
   (fetch> out url {:timeout 2000}))

  ([out url options]
   (fetch> out url options (fn [body] body)))

  ([out url options f]
   (logger/debug "%s - %s - %s - %s" out url options f)
   (client/get url options
               (fn [{:keys [status headers body error]}]
                 (logger/fatal "<<<")
                 (if error
                   (do (logger/warn "Request failed to '%s' with '%s' status." url status)
                       (logger/warn "Body: %s" body))
                   (do
                     (logger/debug "Data fetched from '%s' with '%s'" url options)
                     (put! out (f body))))))))
