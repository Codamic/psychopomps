(ns psychopomps.system
  "System defination of psychopomps."
  (:require [hellhound.system           :as system]
            [hellhound.components.aleph :as aleph]))

(def logger-comp
  {:hellhound.component/name ::logger
   :hellhound.component/start-fn (fn [component context]
                                   (assoc component :instance component))
   :hellhound.component/stop-fn (fn [component]
                                  (dissoc component :instance))})



(defn handler [req]
  {:status 200
   :headers {"content-type" "text/plain"}
   :body "hello!"})

(def dev-system
  {:components [(aleph/factory handler) {:hellhound.component/name ::logger
                                         :hellhound.component/start-fn (fn [this context] this)
                                         :hellhound.component/stop-fn  (fn [this] this)}]
   :workflow [[::aleph/aleph ::logger]]})
