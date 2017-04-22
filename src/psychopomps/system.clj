(ns psychopomps.system
  "System defination of psychopomps."
  (:require [psychopomps.logger      :refer [new-logger]]
            [psychopomps.db          :refer [new-cassandra-client]]
            [psychopomps.collectors.core :refer [new-collector-pool]]
            [psychopomps.jobs.convertors.html :refer [new-html->md-job]]))

(def dev
  {:units {:logger     {:record (new-logger)
                        :started nil}

           ;; :db         {:record (new-cassandra-client)
           ;;              :requires [:logger]
           ;;              :started nil}

           :collectors {:record (new-collector-pool)
                        :requires [:logger]
                        :started nil}

           :html-to-md {:record (new-html->md-job)
                        :requires [:logger :collectors]
                        :started nil
                        :inputs [:collectors]}}})
