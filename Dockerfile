FROM openjdk:8-jre-alpine
WORKDIR /app
COPY target/ .
ENTRYPOINT "java" "-jar" "project.jar"
