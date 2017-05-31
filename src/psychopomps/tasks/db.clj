(ns psychopomps.tasks.db
  (:require [hellhound.tasks.db :as db]
            [hellhound.components.core :as component]
            [psychopomps.system :as system]))


(defn -main
  [& rest]
  (component/set-system! system/dev-system)
  (apply db/main rest))
