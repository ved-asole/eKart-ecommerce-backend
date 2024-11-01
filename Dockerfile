FROM maven:3.9.9-amazoncorretto-17-alpine AS build
ENV REDIS_HOST=host.docker.internal
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8000
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","app.jar"]