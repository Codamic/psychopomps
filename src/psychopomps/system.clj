(ns psychopomps.system
  "System defination of psychopomps."
  (:require
   [hellhound.components.webserver   :as webserver]
   [hellhound.components.websocket   :as websocket]
   [hellhound.components.logger      :refer [new-logger]]
   [psychopomps.web.handlers         :refer [dev-handler]]
   [psychopomps.db                   :refer [new-cassandra-client]]
   [psychopomps.collectors.core      :refer [new-collector-pool]]
   [psychopomps.messaging.router     :refer [message-router]]
   [psychopomps.jobs.convertors.html :refer [new-html->md-job]]))


(def dev-system
  {:components {:logger     {:record (new-logger)
                             :started nil}

                ;; :db         {:record (new-cassandra-client)
                ;;              :requires [:logger]
                ;;              :started nil}

                :websocket {:record (websocket/make-websocket message-router)}
                :webserver {:record (webserver/make-webserver dev-handler)
                            :requires [:websocket]}

                ;; :collectors {:record (new-collector-pool)
                ;;              :requires [:logger]
                ;;              :started nil}

                ;; :cache-to-redis {:record (new-redis-cache)
                ;;                  :requires [:collectors]
                ;;                  :inputs [:collectors]}

                ;; :html-to-md {:record (new-html->md-job)
                ;;              :requires [:logger :collectors]
                ;;              :started nil
                ;;              :inputs [:collectors]}
                }})
