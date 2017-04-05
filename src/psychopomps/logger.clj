(ns psychopomps.logger
  "Clojure core.async style logger."
  (:require [clojure.core.async     :as async]
            [clojure.pprint         :as pprint]
            [io.aviso.ansi          :as ansi]
            [clj-time.core          :as time]
            [clj-time.format        :refer [formatter unparse]]
            [environ.core           :refer [env]]))


(def default-level (keyword (or (env :log-level) "debug")))
(def ^:private log-chan (async/chan 1000))
(def ^:private levels {:trace 1
                       :debug 10
                       :info  100
                       :warn  300
                       :error 500
                       :fatal 1000})
(def ^:private level-colors {:trace ansi/white
                             :debug ansi/cyan
                             :info  ansi/blue
                             :warrn ansi/yellow
                             :error ansi/red
                             :fatal ansi/bold-red})

(defn- get-level
  [level]
  (or (get levels level)
      10))

(defn- render-level
  [level]
  (let [lvl  (clojure.string/upper-case (name level))
        func (get level-colors level)]
    (func lvl)))

(defn- timestamp
  []
  (let [time-format (formatter "yyyy-MM-dd HH:mm:ss:SS")]
    (unparse time-format (time/now))))

(defn- remove-newline
  [arg]
  (if (= 0 (count arg))
    arg
    (subs arg 0 (- (count arg) 1))))

(defn- ->str
  [arg]
  (remove-newline (println-str arg)))

(defn log
  "Log the given string with the given level."
  [level string & rest]
  (if (>= (get-level level)
          (get-level default-level))
    (async/put! log-chan
                {:level level
                 :msg (apply format string (map #(->str %) rest))})))

(defn debug
  [string & rest]
  (apply log :debug string rest))

(defn info
  [string & rest]
  (apply log :info string rest))

(defn warn
  [string & rest]
  (apply log :warn string rest))

(defn error
  [string & rest]
  (apply log :error string rest))

(defn fatal
  [string & rest]
  (apply log :fatal string rest))

(defn stdout-logger
  "Log the `log-chan` info stdout."
  []
  (async/go-loop []
    (let [log-msg (async/<! log-chan)]
      (println
       (format "[%s] <%s>: %s"
               (timestamp)
               (render-level (:level log-msg))
               (:msg log-msg)))
      (recur))))
