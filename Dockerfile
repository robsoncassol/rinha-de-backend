# Use a base image with Java installed
FROM ghcr.io/graalvm/jdk-community:21

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled Spring Boot applic  ation JAR file from the target directory to the container
COPY target/rinha-de-backend-*.jar app.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "-Dspring.aot.enabled=true", "app.jar"]