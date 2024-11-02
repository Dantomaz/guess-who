# Set default value for build-time variable
ARG BUILD_HOME=/.

# Use latest gradle image
FROM gradle:latest AS build
# Use build-time variable
ARG BUILD_HOME
# Copy src folder into the container
COPY ./src $BUILD_HOME/src
# Copy gradle files into the container
COPY build.gradle settings.gradle $BUILD_HOME
# Set working directory inside the container
WORKDIR $BUILD_HOME
# Build the application
RUN gradle clean build


# Use an OpenJDK 21 oracle base image
FROM openjdk:21-jdk-oracle
# Use build-time variable
ARG BUILD_HOME
# Set working directory inside the container
WORKDIR $BUILD_HOME
# Copy the built JAR file from the build stage
COPY --from=build $BUILD_HOME/build/libs/*.jar $BUILD_HOME/app.jar
# Expose the application port
EXPOSE 8080
# Define default command
ENTRYPOINT ["java", "-jar", "app.jar"]
