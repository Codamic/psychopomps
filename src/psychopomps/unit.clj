(ns psychopomps.unit
  "Unit is the same concept as components")

;; Main storage for system data.
(def ^:private default-system (atom {}))

;; Protocols --------------------------------------
(defprotocol Structure
  "This protocol defines the structure of each unit"
  (start [unit])
  (stop  [unit]))

;; Private Functions -------------------------------
(defn- started?
  "Returns true if unit already started"
  [unit-data]
  (:started? unit-data))

(defn- update-started-system
  "Update the system with the given started unit data"
  [system name unit]
  (let [sys (update-in system [:units name :record] unit)]
    (update-in sys [:units name :started? true])))

(defn- update-stopped-system
  "Update the system with the given sopped unit data"
  [system name unit]
  (let [sys (update-in system [:units name :record] unit)]
    (update-in sys [:units name :started? false])))

(defn- start-unit
  "Start the given unit"
  [name data system]
  (if-not (started? data)
    (let [requirements (or (:requires data) [])
          record       (:record   data)]

      (if-not (empty? requirements)
        ;; In case of any requirement we need to start them first
        (doseq [req-name requirements]
          (start-unit req-name (get units req-name) system))

        (let [start-func   (:start record)
              started-unit (start-func record)]
          ;; Replace the record value with the started instance
          (swap! system update-system name started-unit))))))

(defn- stop-unit
  "Stop the given unit"
  [name data system]
  (if (started? data)
    (let [requirements (or (:requires data) [])
          record       (:record   data)]
      (let [stop-func     (:stop record)
            stopped-unit  (stop-func record)]
        (swap! system update-stopped-system name stopped-unit))
      (if-not (empty? requirements)
        (doseq [req-name requirements]
          (stop-unit req-name (get units req-name) system))))))

(defn- iterate-units
  "Iterate over system units"
  [system f]
  (let [units (:units @system)]
    (doseq [[unit-name unit-data] units]
      (f unit-name unit-data system))))

;; Global Functions ---------------------------
(defn set-system!
  "Set the default system"
  [system]
  (swap! default-system (fn [_] system)))

(defn system
  []
  @default-system)

(defn start-system
  "Start the given system and call start on all the units"
  [system]
  (iterate-units system start-unit))

(defn stop-system
  "Stop the given system by calling stop on all units"
  [system]
  (iterate-units system stop-unit))

(defn start
  "Start the default system"
  []
  (start-system default-system))

(defn stop
  "Stop the default system"
  []
  (stop-system default-system))

(defn restart
  "Restart the default system"
  []
  (stop)
  (start))
