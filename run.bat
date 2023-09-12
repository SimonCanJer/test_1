start/wait mvn verify -Dresult.jar=intuit_tst1
start/wait docker build -t intuit_tst1  --build-arg="JAR_NAME=intuit_tst1"  .
start/wait docker run --name intuit_tst1 --env ENV_DATA_SOURCE_FILE=/etc/intuit/data/player.csv -v c:\etc/data:/etc/intuit/data -p 10101:10101 intuit_tst1
