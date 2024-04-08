FROM openjdk:17-jdk
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV    PROFILE prod
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar","/app.jar"]
