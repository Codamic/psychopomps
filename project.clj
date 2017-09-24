(defproject codamic/psychopomps "0.1.0-SNAPSHOT"
  :description "A data collector from all over the internet"
  :url "http://github.com/Codamic/psychopomps"
  :license {:name "GPLv2"
            :url "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :dependencies [[org.clojure/clojure        "1.9.0-alpha20"]
                 [org.clojure/core.async     "0.3.443"]
                 [cheshire                   "5.8.0"]
                 [clj-time                   "0.14.0"]
                 [codamic/hellhound          "1.0.0-SNAPSHOT"]]



  :plugins [[lein-environ      "1.1.0"]
            [io.aviso/pretty   "0.1.33"]]

  :aliases {"db" ["run" "-m" "psychopomps.tasks.db"]}

  :main ^:skip-aot psychopomps.core
  :target-path "target/%s"

  :profiles
  {:dev
   {:env {:environment    :dev
          :news-api-key   "abc4ba4958af471d80d2324761996999"
          :log-level      "debug"
          :redis-pool     nil
          :redis-spec     "redis://localhost:6379/"
          :cassandra-host "localhost"
          :source-paths ["src/" "checkouts/hellhound/src/clj"]}}

   :test {:dependencies [[se.haleby/stub-http "0.2.1"]]}

   :uberjar {:aot :all}})
