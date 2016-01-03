# branch-runner

A continuous delivery pipeline for running git branches of a web service using LambdaCD.

## Requirements
* LambdaCD
* git
* docker
* docker-compose

## Usage

Make sure you can run docker without sudo.

Checkout the git project(s) from github and then set the values in config.clj

* `lein run` will start your pipeline with a web-ui listening on port 8080

## Files

* `pipeline.clj` contains pipeline-definition
* `steps.clj` contains build-steps

* `config.clj` contains the configs for this setup

## TODO

* fetch remote branches from local repo instead of github api
* generate a list of ports for each stack instead of one
* move docker commands into config instead of in steps
* add contexts dynamically when branches are created

Index page:
* show dates for git and docker run
* style list
