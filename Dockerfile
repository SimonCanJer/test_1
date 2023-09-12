FROM openjdk:18-oracle

ARG JAR_NAME=intuit-test
RUN mkdir -p /etc/intuit/sources

COPY target/$JAR_NAME.jar /etc/intuit/intuit.jar

COPY sources/player.csv  /etc/intuit/sources

EXPOSE 10101

ENTRYPOINT ["java","-Xmx16g","-jar","/etc/intuit/intuit.jar"]
