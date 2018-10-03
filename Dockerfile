FROM openjdk:10-jre-slim
COPY ./target/project0-0.0.1-SNAPSHOT.jar .
WORKDIR .
EXPOSE 8080
CMD ["java", "-jar", "project0-0.0.1-SNAPSHOT.jar"]
