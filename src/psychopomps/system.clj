(ns psychopomps.system
  "System defination of psychopomps."
  (:require
   [hellhound.components.webserver   :as webserver]
   [hellhound.components.websocket   :as websocket]
   [hellhound.components.logger      :refer [new-logger]]
   [psychopomps.web.handlers         :refer [dev-handler]]
   [hellhound.components.cassandra   :as cassandra]
   [psychopomps.collectors.core      :refer [new-collector-pool]]
   [psychopomps.messaging.router     :refer [message-router]]
   [psychopomps.jobs.convertors.html :refer [new-html->md-job]]
   [psychopomps.cache.component :refer [new-redis-cache]]))


(def dev-system
  {:components {:logger     {:record (new-logger)
                             :started nil}

                ;; This name should be the same as configuration key in the
                ;; Application configuration under the `:db` keyboard
                :cassandra  {:record (cassandra/make-cassandra-client)
                             :requires [:logger]
                             :started nil}

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
