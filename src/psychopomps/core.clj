(ns psychopomps.core
  "Psychopomps is the godess who guide the souls to underworld."
  (:require [clojure.core.async        :as async]
            [hellhound.logger          :as logger]
            [hellhound.system          :as hellhound]
            [psychopomps.system        :as system])

  (:gen-class))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (hellhound/set-system! system/dev-system)
  (hellhound/start!))


(defn stop-system
  []
  (hellhound/stop!))

(.addShutdownHook
 (Runtime/getRuntime)
 (Thread.
  (fn []
    (println "Shutting down...")
    (stop-system))))
