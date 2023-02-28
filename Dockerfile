FROM amazoncorretto:17-alpine
EXPOSE 8080
ADD target/mqtt2influxdb-wejago.jar mqtt2influxdb-wejago.jar
ENTRYPOINT ["java","-jar","/mqtt2influxdb-wejago.jar"]
