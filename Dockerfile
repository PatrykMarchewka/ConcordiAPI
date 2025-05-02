# Use an official OpenJDK runtime as a parent image (Java 21 version)
FROM eclipse-temurin:21-jdk-alpine as builder

# Set the working directory in the container
WORKDIR /app

# Copy Gradle wrapper files and build files to the container
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Copy your source code into the container
COPY src src

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Build the app using Gradle
RUN ./gradlew build -x test --no-daemon

# Use a smaller JDK runtime to run the app
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR from the builder image
COPY --from=builder /app/build/libs/ConcordiAPI-1.0.0.jar /app/ConcordiAPI-1.0.0.jar

# Expose port 10000 (as you're using this custom port)
EXPOSE 10000

# Run the application, setting the port to 10000
ENTRYPOINT ["java", "-Dserver.port=10000", "-jar", "/app/ConcordiAPI-1.0.0.jar"]
