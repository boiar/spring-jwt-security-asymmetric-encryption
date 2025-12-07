# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jdk

ARG PROFILE=dev
ARG APP_VERSION=1.0.0

WORKDIR /app
COPY --from=build /app/target/*.jar /app/

EXPOSE 8080

ENV DB_URL=jdbc:postgresql://postgres-sql-spring-app:5432/spring_app_db
ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}

CMD ["java","-jar","/app/target/auth-security-0.0.1-SNAPSHOT.jar","--spring.profiles.active=${ACTIVE_PROFILE}"]
