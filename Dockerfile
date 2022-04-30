FROM openjdk:8-jre-alpine
WORKDIR /app
COPY target/ .
COPY entrypoint.sh .
ENTRYPOINT "sh" "entrypoint.sh"
# ENTRYPOINT "java" "-jar" "project.jar"
