FROM amazoncorretto:17-alpine
EXPOSE 8080
ADD ./target/mqtt2influxdb-wejago.jar mqtt2influxdb-wejago.jar
ADD ./src/main/resources/mqtt2influx-configuration.yaml mqtt2influx-configuration.yaml
ENTRYPOINT ["java","-jar","/mqtt2influxdb-wejago.jar"]
