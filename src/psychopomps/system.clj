(ns psychopomps.system
  "System defination of psychopomps."
  (:require
   [hellhound.events                 :refer [defrouter]]
   [hellhound.components.webserver   :as webserver]
   [hellhound.components.websocket   :as websocket]
   [hellhound.components.logger      :refer [new-logger]]
   [psychopomps.web.handlers         :refer [dev-handler]]
   [psychopomps.db                   :refer [new-cassandra-client]]
   [psychopomps.collectors.core      :refer [new-collector-pool]]
   [psychopomps.jobs.convertors.html :refer [new-html->md-job]]))



(defrouter event-router
  {:event1 (fn [data _] (println data))})

(def dev
  {:components {:logger     {:record (new-logger)
                             :started nil}

                ;; :db         {:record (new-cassandra-client)
                ;;              :requires [:logger]
                ;;              :started nil}

                :websocket {:record (websocket/make-websocket event-router)}
                :webserver {:record (webserver/make-webserver dev-handler)
                            :requires [:websocket]}

                ;; :collectors {:record (new-collector-pool)
                ;;              :requires [:logger]
                ;;              :started nil}

                ;; :html-to-md {:record (new-html->md-job)
                ;;              :requires [:logger :collectors]
                ;;              :started nil
                ;;              :inputs [:collectors]}
                }})
