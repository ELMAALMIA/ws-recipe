FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

# Use the PORT environment variable that Vercel provides
ENV PORT=8080

CMD ["java", "-Dserver.port=${PORT}", "-jar", "target/*.jar"]