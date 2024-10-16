FROM openjdk:21-oracle
COPY ./target/authentication-service-0.0.1-SNAPSHOT.jar authentication-service.jar
CMD ["java","-jar","authentication-service.jar"]
