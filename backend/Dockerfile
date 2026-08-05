FROM eclipse-temurin:24-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
