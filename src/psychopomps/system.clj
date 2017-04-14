(ns psychopomps.system
  "System namespace which contains dev and production systems."
  (:require [com.stuartsierra.component       :as component]
            [psychopomps.db                   :refer [new-db-server]]
            [psychopomps.collectors.component :refer [new-collector-pool]]
            [psychopomps.logger               :refer [new-logger]]
            (system.components
             [http-kit                        :refer [new-web-server]])))

(defn dev-system
  "Assemble the development system"
  []
  (component/system-map
   :logger    (new-logger)
   :db        (new-db-server)
   :collector (new-collector-pool)))
