FROM openjdk:17-jdk-alpine

# Change directory
WORKDIR /app

# Copy war file
COPY target/team-service-0.0.1-SNAPSHOT.war team-service.war

# Create non-root user
RUN adduser -D team_service
RUN chown -R team_service:team_service /app
USER team_service

# Run app
ENTRYPOINT ["sh","-c","java -jar -Dspring.config.location=${CONFIG_PATH} team-service.war"]

# Expose port 8081
EXPOSE 8081