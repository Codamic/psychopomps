(ns psychopomps.logger
  "Clojure core.async style logger."
  (:require [clojure.core.async     :as async]
            [clojure.pprint         :as pprint]
            [io.aviso.ansi          :as ansi]
            [clj-time.core          :as time]
            [clj-time.format        :refer [formatter unparse]]
            [environ.core           :refer [env]]
            [system.repl            :refer [system]]
            [com.stuartsierra.component :as component]))


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
  [level string & rest]
  (if (>= (get-level level)
          (get-level default-level))
    (let [chan (:channel (:logger system))]
      (if (chan)
        (async/put! (:logger system)
                    {:level level
                     :msg (apply format string (map #(->str %) rest))})
        (println "You have to run the `logger` system.")))))

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

(defn start-logger
  "Log the `log-chan` info stdout."
  [channel]
  (async/thread
    (loop []
      (let [log-msg (async/take! channel)]
        (println
         (format "[%s] <%s>: %s"
                 (timestamp)
                 (render-level (:level log-msg))
                 (:msg log-msg)))
        (recur)))))

(defn stop-logger
  "Stop the logger activity"
  [log-chan]
  (async/close! log-chan))


(defrecord Logger []
  component/Lifecycle
  (start [this]
    (let [log-chan (async/chan 1000)]
      (start-logger log-chan)
      (assoc this :channel log-chan)))

  (stop [this]
    (if (:channel this)
      (stop-logger (:channel this))
      (this))))


(defn new-logger
  "Create a new logger instance from `Logger` component."
  []
  (->Logger))
