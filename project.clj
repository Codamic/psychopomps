(defproject codamic/psychopomps "0.1.0-SNAPSHOT"
  :description "A data collector from all over the internet"
  :url "http://github.com/Codamic/psychopomps"
  :license {:name "GPLv2"
            :url "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :dependencies [[org.clojure/clojure    "1.9.0-alpha15"]
                 [org.clojure/core.async "0.3.442"]
                 [cheshire               "5.7.0"]
                 [environ                "1.1.0"]
                 [clj-oauth              "1.5.5"]
                 [clj-http               "3.4.1"]
                 [io.aviso/pretty        "0.1.33"]
                 [clj-time               "0.13.0"]]

  :plugins [[lein-environ "1.1.0"]
            [io.aviso/pretty "0.1.33"]]

  :main ^:skip-aot psychopomps.core
  :target-path "target/%s"
  :profiles {:dev {:env {:news-api-key "abc4ba4958af471d80d2324761996999"
                         :log-level    :debug}
                   :dependencies []}

             :uberjar {:aot :all}})
