(defproject codamic/psychopomps "0.1.0-SNAPSHOT"
  :description "A data collector from all over the internet"
  :url "http://github.com/Codamic/psychopomps"
  :license {:name "GPLv2"
            :url "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :dependencies [[org.clojure/clojure        "1.9.0-alpha15"]
                 [org.clojure/core.async     "0.3.442"]
                 [cheshire                   "5.7.1"]
                 [com.taoensso/carmine       "2.16.0"]
                 [environ                    "1.1.0"]
                 [clj-oauth                  "1.5.5"]
                 [http-kit                   "2.2.0"]
                 [hickory                    "0.7.1"]
                 [io.aviso/pretty            "0.1.33"]
                 [clj-time                   "0.13.0"]
                 [codamic/hellhound          "0.14.0-SNAPSHOT"]
                 [clojurewerkz/cassaforte    "3.0.0-alpha1"]
                 [com.stuartsierra/component "0.3.2"]
                 [org.danielsz/system        "0.4.0"]]

  :plugins [[lein-environ      "1.1.0"]
            [io.aviso/pretty   "0.1.33"]]

  :aliases {"db" ["run" "-m" "hellhound.tasks.db"]}

  :main ^:skip-aot psychopomps.core
  :target-path "target/%s"
  :profiles {:dev
             {:env {:news-api-key "abc4ba4958af471d80d2324761996999"
                    :log-level    "debug"
                    :redis-pool nil
                    :redis-spec "redis://localhost:6379/"}

              :source-paths ["src/" "checkouts/hellhound/src/clj"]
              :dependencies []}

             :test {:dependencies [[se.haleby/stub-http "0.2.1"]]}

             :uberjar {:aot :all}})
