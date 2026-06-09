FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/HBO-ICT-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/src/main/resources/electionData ./electionData

EXPOSE 8002
ENTRYPOINT ["java", "-jar", "app.jar"]
