# Start with a base image containing Java runtime
FROM amazoncorretto:17 AS build

# The application's .jar file
ARG JAR_FILE=target/*.jar

# cd into the target directory
WORKDIR /usr/src/app

# Copy the application's jar to the container
COPY ${JAR_FILE} app.jar

# Execute the application
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/usr/src/app/app.jar"]