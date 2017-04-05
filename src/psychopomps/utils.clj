(ns psychopomps.utils
  "A namespace for utility functions"
  (:require [clojure.core.async  :refer [put!]]
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

(defn fetch>
  "Fetch the given url data and and put it into `out` channel"
  ([out url]
   (fetch> out url {:timeout 2000}))

  ([out url options]
   (client/get url options
               (fn [{:keys [status headers body error]}]
                 (if error
                   (do (logger/warn "Request failed to '%s' with '%s' status." url status)
                       (logger/warn "Body: %s" body))
                   (put! out body))))))
