(ns psychopomps.logger
  "Clojure core.async style logger."
  (:require [clojure.core.async     :as async]
            [clojure.pprint         :as pprint]
            [io.aviso.ansi          :as ansi]
            [clj-time.core          :as time]
            [clj-time.format        :refer [formatter unparse]]
            [environ.core           :refer [env]]
            [psychopomps.unit       :as unit]))


(def default-level (keyword (or (env :log-level) "debug")))

;(def ^:private log-chan (async/chan 1000))

(def ^:private levels {:trace 1
                       :debug 10
                       :info  100
                       :warn  300
                       :error 500
                       :fatal 1000})

(def ^:private level-colors {:trace ansi/white
                             :debug ansi/green
                             :info  ansi/blue
                             :warn ansi/yellow
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
  [chan level string & rest]
  (if (>= (get-level level)
          (get-level default-level))
    (if (chan)
      (async/put! (unit/get-unit :logger)
                  {:level level
                   :msg (apply format string (map #(->str %) rest))})
      (println "You have to run the `logger` system."))))

(defn debug
  [string & rest]
  (let [log (:log (unit/get-unit :logger))]
    (apply log :debug string rest)))

(defn info
  [string & rest]
  (let [log (:log (unit/get-unit :logger))]
    (apply log :info string rest)))

(defn warn
  [string & rest]
  (let [log (:log (unit/get-unit :logger))]
    (apply log :warn string rest)))

(defn error
  [string & rest]
  (let [log (:log (unit/get-unit :logger))]
    (apply log :error string rest)))

(defn fatal
  [string & rest]
  (let [log (:log (unit/get-unit :logger))]
    (apply log :fatal string rest)))

(defn start-logger
  "Log the `log-chan` info stdout."
  [channel]
  (async/thread
    (loop [log-msg  (async/<!! channel)]
      (when log-msg
        (println
         (format "[%s] <%s>: %s"
                 (timestamp)
                 (render-level (:level log-msg))
                 (:msg log-msg)))
        (recur (async/<!! channel))))))

(defn stop-logger
  "Stop the logger activity"
  [log-chan]
  (async/close! log-chan))


(defrecord Logger []
  unit/Structure
  (start [this]
    (let [log-chan (async/chan 1000)]

      (start-logger log-chan)
      (assoc this
             :channel log-chan
             :log (fn [chan & rest] (apply log log-chan rest)))))

  (stop [this]
    (if (:channel this)
      (do
        (stop-logger (:channel this))
        (dissoc this :channel :log))
      this)))


(defn new-logger
  "Create a new logger instance from `Logger` component."
  []
  (->Logger))
