# branch-runner

A continuous delivery pipeline for running git branches of a web service using LambdaCD.

## Requirements
* docker
* docker-compose
* LambdaCD

## Usage

Make sure you can run docker without sudo.

Checkout the git project(s) from github and then set the values in config.clj

* `lein run` will start your pipeline with a web-ui listening on port 8080

## Files

* `pipeline.clj` contains pipeline-definition
* `steps.clj` contains build-steps

* `config.clj` contains the configs for this setup

## TODO

* add contexts dynamically when branches are created

Index page:
* show dates for git and docker run
* style list
