(ns psychopomps.tasks.db
  (:require [qbits.alia   :as alia]
            [environ.core :refer [env]]
            [qbits.hayt   :refer [create-keyspace with]]))

(defn connect
  []
  (let [host    (env :cassandra-host)
        cluster (alia/cluster {:contact-points [host]
                               :load-balancing-policy :default})]
    (alia/connect cluster)))

(defn setup-db
  [session]
  (let [keyspace-name (env :cassandra-keyspace-name)]
    (alia/execute session
                  (create-keyspace keyspace-name
                                   (with {:replication {:class "SimpleStrategy"
                                                        :replication_factor 1}})))))

(defn -main
  [cmd & rest]
  (let [session (connect)]
    (cond
      (= cmd "create")  (setup-db session)
      (= cmd "migrate") (migrate session)
      :else             (print "Use either 'migrate' or 'rollback"))))
