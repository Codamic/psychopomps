(ns psychopomps.utils-test
  (:require [psychopomps.utils :as sut]
            [clojure.core.async :refer [go <!! chan]]
            ;; [stub-http.core :refer :all]
            [cheshire.core  :refer [generate-string]]
            [clojure.test :refer :all]))

;; (def RES {:status "ok"
;;           :sources [{:id "1"} {:id "2"}]})

;; (deftest fetch>-test []
;;   (with-routes!
;;     {"/sources" {:status 200
;;                  :content-type "application/json"
;;                  :body (generate-string RES)}}
;;     (let [subject sut/fetch>
;;           out     (chan)
;;           url     "/sources"
;;           resutl  (go (subject out url {}))]

;;       (is (= RES (<!! out))))))
