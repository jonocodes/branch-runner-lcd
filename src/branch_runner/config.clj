(ns branch-runner.config)

(def service-name "Test App")

; (def base-dir "/home/jfinger/ownCloud/lambdaCd/")
(def base-dir "/home/jono/files/lambdacd")

(def remote-repo "git@github.com:jonocodes/lcd-test-app")

(def lambdacd-project-dir (str base-dir "/branch-runner-lcd"))

(def dockerfiles-dir (str base-dir "/lcd-test-app"))

(def local-git-dir (str base-dir "/lcd-test-app"))

(defn build-stack-command []
  "docker build -t lcdapp .")

(defn start-stack-command [envs branch port]
  (format "WEB_PORT=%d docker-compose -p %s up -d" port branch))

(defn stop-stack-command [envs branch port]
  (format "WEB_PORT=%d docker-compose -p %s stop" port branch))

; TODO: generate this from compose file
(def required-ports {
    :web {
        :WEB_PORT 0
        :PORT_HTTP_WEB 0
    },
    :redis {
      :PORT_TCP_REDIS 0
    }
  })
