FROM amazoncorretto:17-alpine
EXPOSE 8080
ADD ./target/mqtt2kafka-wejago.jar mqtt2kafka-wejago.jar
ENTRYPOINT ["java","-jar","/mqtt2kafka-wejago.jar"]
