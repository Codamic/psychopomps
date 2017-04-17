(ns psychopomps.db
  "This namespace provides the necessary means to communicate with
  database."
  (:require [clojurewerkz.cassaforte.client :as client]
            [environ.core                   :as environ]
            [psychopomps.unit               :as unit]
            [psychopomps.logger             :as logger]))


(defrecord CassandraClient [host]
  unit/Structure
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

(defn new-cassandra-client
  "Create a new instance of `CassandraClient` component."
  []
  (let [host (or (environ/env :cassandra-host)
                 ["127.0.0.1"])]
    (->CassandraClient host)))
