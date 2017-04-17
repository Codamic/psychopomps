(ns psychopomps.system
  "System defination of psychopomps."
  (:require [psychopomps.logger      :refer [new-logger]]
            [psychopomps.db          :refer [new-cassandra-client]]
            [psychopomps.collectors.core :refer [new-collector-pool]]))

(def dev-system
  (atom {:units {:logger     {:record (new-logger)
                              :started nil}

                 :db         {:record (new-cassandra-client)
                              :requires [:logger]
                              :started nil}

                 :collectors {:record (new-collector-pool)
                              :requires [:logger :db]
                              :started nil}}}))
