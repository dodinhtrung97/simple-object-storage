FROM java:8
EXPOSE 8080

ADD ./src/main/resources/application.properties ./application.properties
ADD ./target/project0-0.0.1-SNAPSHOT.jar /webapi/project0-0.0.1-SNAPSHOT.jar
WORKDIR /webapi
VOLUME /webapi/bucket
CMD ["java", "-jar", "./project0-0.0.1-SNAPSHOT.jar"]