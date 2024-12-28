FROM maven:3.9.9-eclipse-temurin-23 AS builder

ARG APP_DIR=/myapp

WORKDIR ${APP_DIR}

COPY src src
COPY .mvn .mvn
COPY pom.xml .
COPY mvnw .

RUN mvn clean package -Dmaven.test.skip=true

FROM maven:3.9.9-eclipse-temurin-23

COPY --from=builder /myapp/target/FlavourFoundry-0.0.1-SNAPSHOT.jar app.jar
COPY recipes.json .

ENV SERVER_PORT=8080

EXPOSE ${SERVER_PORT}

ENTRYPOINT java -Duser.timezone=Asia/Singapore -jar app.jar