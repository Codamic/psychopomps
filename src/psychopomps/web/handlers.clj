(ns psychopomps.web.handlers
  (:require [hellhound.routes.core          :refer [make-handler route-table hellhound-routes redirect-to-not-found]]
            [hellhound.middlewares.core     :refer :all]))


(def routes (make-handler
             ["/" [(hellhound-routes)
                   (redirect-to-not-found)]]))


(def dev-handler (-> #'routes
                     wrap-development-kit))

(def handler (-> #'routes
                 wrap-production-kit))
