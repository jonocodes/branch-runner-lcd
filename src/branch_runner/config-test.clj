(ns branch-runner.config)

(def service-name "Test App")

(def base-dir "/home/jfinger/ownCloud/lambdaCd/")
; (def base-dir "/home/jono/files/lambdacd")

(def remote-repo "git@github.com:jonocodes/lcd-test-app")

(def lambdacd-project-dir (str base-dir "/branch-runner-lcd"))

(def lambdacd-dockerfiles-dir (str base-dir "/lcd-test-app"))

(def local-git-dir (str base-dir "/lcd-test-app"))

; (def github-api-branch-list "https://api.github.com/repos/jonocodes/lcd-test-app/branches")

; (def projects [{:pipeline-url "/develop"
;                 :branch "develop"
;                 :port   2222}
;                {:pipeline-url "/feature-foo"
;                 :branch "feature-foo"
;                 :port   3333}
;                 ])
