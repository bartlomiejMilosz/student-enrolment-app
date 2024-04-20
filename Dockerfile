# Start with a base image containing Java runtime
FROM openjdk:17-alpine

# Add Maintainer Info
LABEL maintainer="Bart Milo"

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/app-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} student-enrolment-app.jar

# Run the jar file 
ENTRYPOINT ["java","-jar","/student-enrolment-app.jar"]
