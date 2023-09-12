#!/bin/bash
mvn verify -Dresult.jar=intuit_test && start/wait docker build -t intest  --build-arg="JAR_NAME=intuit_test" . && start/wait docker run --name intest --env ENV_DATA_SOURCE_FILE=/etc/intuit/data/player.csv -v c:\etc/data:/etc/intuit/data -p 10101:10101 intest
