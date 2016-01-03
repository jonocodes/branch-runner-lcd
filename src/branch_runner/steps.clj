(ns branch-runner.steps
  (:require [lambdacd.steps.shell :as shell])
  (:require [lambdacd.steps.git :as git])
  (:use [branch-runner.config]))

(defn wait-for-remote-repo [branch]
  (fn [args ctx]
    (git/wait-for-git ctx remote-repo branch)))

(defn update-git-repo [branch]
  (fn [args ctx]
    (shell/bash ctx lambdacd-project-dir
      (format "cd %s" local-git-dir)
      (format "git checkout %s" branch)
      "git pull")))

(defn build-stack [dir]
  (fn [args ctx]
    (shell/bash ctx dir
      (build-stack-command))
    {:status :success}))

(defn start-stack [envs dir branch port]
  (fn [args ctx]
    (shell/bash ctx dir
      (start-stack-command envs branch port))
    {:status :success}))

(defn stop-stack [envs dir branch port]
  (fn [args ctx]
    (shell/bash ctx dir
      (stop-stack-command envs branch port))
    {:status :success}))
