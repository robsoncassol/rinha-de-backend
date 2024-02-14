# Use a base image with Java installed
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled Spring Boot application JAR file from the target directory to the container
COPY target/rinha-de-backend-*.jar app.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "-Xms128m" , "-Xmx180m", "-XX:NewSize=180m", "-XX:MaxNewSize=180m", "-XX:SurvivorRatio=12", "app.jar"]
