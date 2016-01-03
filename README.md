# branch-runner

A continuous delivery pipeline for running git branches of a web service using LambdaCD.

## Requirements

* LambdaCD
* git
* docker
* docker-compose

## Usage

Make sure you can run docker without sudo.

* Checkout the git project(s) from github and then set the values in config.clj

* `lein run` will start your pipeline with a web-ui listening on port 8080

## TODO

* generate a list of ports for each stack instead of one

Index page:
* show dates for git and docker run
* style list
