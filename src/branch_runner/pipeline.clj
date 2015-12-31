(ns branch-runner.pipeline
  (:use [lambdacd.steps.control-flow]
        [lambdacd.steps.manualtrigger]
        [branch-runner.steps]
        [branch-runner.config]
        [clojure.java.io]
        [clojure.java.shell :only [sh]]
        )
  (:require
        [ring.server.standalone :as ring-server]
        [lambdacd.ui.ui-server :as ui]
        [lambdacd.runners :as runners]
        [lambdacd.util :as util]
        [lambdacd.core :as lambdacd]
        [compojure.core :as compojure]
        [hiccup.core :as h]
        [clojure.tools.logging :as log]
        [lambdacd.steps.manualtrigger :as manualtrigger]
        [lambdacd.steps.support :refer [capture-output]]
        [clojure.data.json :as json])
  (:gen-class))

(def running-branches
  []
  )

(println (count (clojure.string/split-lines (:out (sh "sh" "-c" "docker ps --format '{{.Names}}\t{{.Ports}}' | grep _web_1 | sed 's/_web_1//' | sed 's/0.0.0.0://' | sed 's/->.*//'")))))

; docker ps --format '{{.Names}}\t{{.Ports}} | grep _web_1 | sed 's/0.0.0.0\:\([0-9]\+\)->5000\/tcp/\1/' | sed 's/_web_1//'

(def remote-branches
  (map #(get % "name")
  (json/read-str (slurp (str lambdacd-project-dir "/api-fetch-temp.json")))
))

(print remote-branches)

(defn mk-projects []
  [
  {
    :pipeline-url "/develop"
    :branch "develop"
    :port   2222}
  {
    :pipeline-url "/feature-foo"
    :branch "feature-foo"
    :port   3333}
  {
    :pipeline-url (str (new java.util.Date))
    :branch "now"
    :port 4444
  }
])

(defn mk-pipeline-def [{branch :branch port :port}]
  `(
    (either
      manualtrigger/wait-for-manual-trigger
      (alias "wait for git repo"
        (wait-for-remote-repo ~branch)))
    (update-git-repo ~branch)
    (in-parallel
      build-docker-image)
    (stop-docker ~branch ~port)
    (start-docker ~branch ~port)))

(defn pipeline-for [project]
  (print "  creating pipeline-for")
  (println project)
  (let [home-dir     (util/create-temp-dir)
        config       { :home-dir home-dir :dont-wait-for-completion false}
        pipeline-def (mk-pipeline-def project)
        pipeline     (lambdacd/assemble-pipeline pipeline-def config)
        app          (ui/ui-for pipeline)]
    (runners/start-one-run-after-another pipeline)
    app))

(defn mk-context [project]
  (let [app (pipeline-for project)] ; don't inline this, otherwise compojure will always re-initialize a pipeline on each HTTP request
    (compojure/context (:pipeline-url project) [] app)))

(defn mk-contexts []
  (map mk-context (mk-projects)))

;; Nice overview page:
(defn mk-link [{url :pipeline-url branch :branch port :port}]
  [:li [:a {:href (str url "/")} branch]
    " -> "
    [:a {:href (format "http://localhost:%d" port)} (format "localhost:%d" port)]])

(defn mk-index []
  ; (println "Index projects: ")
  ; (println (mk-projects))
  (h/html
    [:html
     [:head
      [:title service-name " Pipelines"]]
     [:body
      [:h1 service-name " Pipelines"]
      [:ul (map mk-link (mk-projects))]]]))

(defn branch-page [name]
  ; (print name)
  (println "new pipeline: " (pipeline-for {:branch name  :port 4444}))
  (str "branch " name)
  ; (pipeline-for {:branch name  :port 4444})
  )

(defn -main [& args]
  (let [
        contexts (map mk-context (mk-projects))
        ; contexts (mk-contexts)
        routes (apply compojure/routes
                      (conj
                        contexts
                        (compojure/GET "/branch/:name" [name] (branch-page name))
                        (compojure/GET "/" [] (mk-index))))]
       (ring-server/serve routes {:open-browser? false
                               :port 8080})))
