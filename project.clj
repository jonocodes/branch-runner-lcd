(defproject branch-runner "0.1.0-SNAPSHOT"
            :description "Run all the branches of a project as seperate services"
            :url "https://github.com/jonocodes/branch-runner-lcd"
            :dependencies [[lambdacd "0.6.0"]
                           [ring-server "0.3.1"]
                           [org.clojure/clojure "1.7.0"]
                           [org.clojure/tools.logging "0.3.0"]
                           [org.slf4j/slf4j-api "1.7.5"]
                           [org.clojure/data.json "0.2.6"]
                          ;  [clj-yaml "0.4"]
                           ]
            :profiles {:uberjar {:aot :all}}
            :main branch-runner.pipeline)
