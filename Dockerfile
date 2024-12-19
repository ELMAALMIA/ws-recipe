# Use the Eclipse Temurin JDK 17 Alpine image as the base image
FROM eclipse-temurin:17-jdk-alpine

# Create a temporary directory for the application
VOLUME /tmp

# Copy the JAR file from the target directory to the root of the image
COPY target/recipe-generator-0.0.1-SNAPSHOT.jar /ascii-art-generator.jar

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/ascii-art-generator.jar"]
