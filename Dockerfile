FROM openjdk:17-jdk-alpine

## Change directory
WORKDIR /app

## Create non-root user
RUN adduser -D team_service
RUN chown -R team_service:team_service /app
USER team_service

## Copy war file and run app
COPY target/team-service-0.0.1-SNAPSHOT.war team_service.war
ENTRYPOINT ["java","-jar","team_service.war"]

## Expose port 8081
EXPOSE 8081