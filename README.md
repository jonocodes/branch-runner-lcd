# branch-runner

A continuous delivery pipeline for running git branches of a web service using LambdaCD.

## Usage

First set checkout the git project(s) from github and then set the values in config.clj

* `lein run` will start your pipeline with a web-ui listening on port 8080

## Files

* `pipeline.clj` contains your pipeline-definition
* `steps.clj` contains your custom build-steps

* `config.clj` contains the configs for this setup