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

(defn parse-int [s]
  (Integer. (re-find #"[0-9]*" s)))

(defn get-unused-port []
  ; TODO: choose unused port
  ; https://github.com/technomancy/server-socket/blob/master/src/server/socket.clj
  ; http://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
  (+ 6000 (rand-int 5000)))

(defn get-running-branches []
  (let [docker-list (clojure.string/replace (:out (sh "sh" "-c" "docker ps --format '{{.Names}}\t{{.Ports}}' | grep _akita_1 | sed 's/_akita_1//' | sed 's/0.0.0.0://' | sed 's/->.*//'")) #"\n" "\t")]
    (if (= 0 (count docker-list)) {}
      (apply hash-map (.split docker-list"\t")))))

(defn get-remote-branches []  ; TODO: fetch from github
  (map #(get % "name")
    (json/read-str (slurp (str lambdacd-project-dir "/api-fetch-temp-akita.json")))))

(defn docker-namify [branch]  ; turn into a name that is a valid for a docker container
  (clojure.string/replace branch #"[^a-zA-Z0-9_]" ""))

(println "running branches: " (get-running-branches))

(println "remote branches: " (get-remote-branches))

(defn mk-projects []
  (let [running-branches (get-running-branches)]
    (map #(hash-map
      :pipeline-url (format "/%s" %)
      :branch %
      :port (if (= nil (get running-branches (docker-namify %)))
        (get-unused-port)
        (parse-int (get running-branches (docker-namify %)))
        )
      :running (not= nil (get running-branches (docker-namify %)))
    ) (get-remote-branches))))

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

;; Index page
(defn mk-link [{pipeline-url :pipeline-url branch :branch port :port running :running}]
  ; TODO: display date
  [:li [:a {:href (str pipeline-url "/")} branch]
    " -> "
    (if running
      [:a {:href (format "http://localhost:%d" port)} (format "localhost:%d" port)]
      "not running")])

(defn mk-index []
  (h/html
    [:html
     [:head
      [:meta {:HTTP-EQUIV "refresh" :CONTENT "2"}]
      [:title service-name " Pipelines"]]
     [:body
      [:h1 service-name " Pipelines"]
      [:ul (map mk-link (mk-projects))]]]))

(defn branch-page [name] ; TODO: get this to work
  (println "new pipeline: " name)
  ; (println (pipeline-for {:branch name  :port (get-unused-port)}))
  ; (str "branch " name)
  (pipeline-for {:branch name  :port (get-unused-port)}))

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
