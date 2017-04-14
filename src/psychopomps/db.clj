(ns psychopomps.db
  "This namespace provides the necessary means to communicate with
  database."
  (:require [clojurewerkz.cassaforte.client :as client]
            [environ.core                   :as environ]
            [com.stuartsierra.component     :as component]
            [psychopomps.logger             :as logger]))


(defrecord CassandraServer [host]
  component/Lifecycle
  (start [this]
    (logger/info "Connecting to Cassandra cluster...")
    (assoc this :session (client/connect host)))

  (stop [this]
    (if (:session this)
      (do
        (logger/info "Disconnecting from Cassandra cluster...")
        (client/disconnect (:session this))
        (dissoc this :session))
      this)))

(defn new-db-server
  "Create a new instance of `CassandraServer` component."
  []
  (let [host (or (environ/env :cassandra-host)
                 ["127.0.0.1"])]
    (->CassandraServer host)))
