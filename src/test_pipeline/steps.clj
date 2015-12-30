(ns test-pipeline.steps
  (:require [lambdacd.steps.shell :as shell])
  (:require [lambdacd.steps.git :as git])
  (:use [test-pipeline.config]))

(defn wait-for-testapp-repo [branch]
  (fn [args ctx]
    (git/wait-for-git ctx testapp-repo branch)))

(defn update-git-repo [branch]
  (fn [args ctx]
    (shell/bash ctx lambdacd-project-dir
      (format "cd %s" local-git-dir)
      (format "git checkout %s" branch)
      "git pull")))

(defn build-testapp-image [args ctx]
  (shell/bash ctx lambdacd-dockerfiles-dir
    "docker build -t lcdapp ."))

(defn start-docker [branch port]
  (fn [args ctx]
    (shell/bash ctx lambdacd-dockerfiles-dir
      (format "WEB_PORT=%d docker-compose -p %s up -d" port branch))
    {:status :success}))

(defn stop-docker [branch port]
  (fn [args ctx]
    (shell/bash ctx lambdacd-dockerfiles-dir
      (format "WEB_PORT=%d docker-compose -p %s stop" port branch))
    {:status :success}))
