(defproject test-pipeline "0.1.0-SNAPSHOT"
            :description "Run all the branches of a project as seperate services"
            :url "http://example.com/FIXME"
            :dependencies [[lambdacd "0.6.0"]
                           [ring-server "0.3.1"]
                           [org.clojure/clojure "1.7.0"]
                           [org.clojure/tools.logging "0.3.0"]
                           [org.slf4j/slf4j-api "1.7.5"]
                          ; [org.slf4j/slf4j-log4j12 "1.7.1"]
                          ; [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                          ;          javax.jms/jms
                          ;          com.sun.jmdk/jmxtools
                          ;          com.sun.jmx/jmxri]]

                          ;  [ch.qos.logback/logback-core "1.0.13"]
                          ;  [ch.qos.logback/logback-classic "1.0.13"]
                          ]
            :profiles {:uberjar {:aot :all}}
            :main test-pipeline.pipeline)
