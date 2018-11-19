FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8080
ADD build/libs/shift-service-*.jar shift-service.jar

ENTRYPOINT ["sh","-c","java -jar /shift-service.jar"]