FROM maven:3.8.5-openjdk-17

WORKDIR /rating

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean
RUN mvn package -DskipTests

FROM openjdk:17-jdk

COPY /target/rating-microservice*.jar /rating/launch-rating.jar

ENTRYPOINT ["java","-jar","/rating/launch-rating.jar"]

EXPOSE 8084