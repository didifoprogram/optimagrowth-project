#stage 1
#Start with a base image containing Java runtime
FROM openjdk:11-slim as build

#Add Maintainer Info
LABEL maintainer="Diego <diego@email.com>"

# The application's jar file
ARG JAR_FILE

# Add the app jar to the container
COPY ${JAR_FILE} app.jar

# Unpack jar file
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

# Stage 2
# Same Java runtime
FROM openjdk:11-slim

# Add volume pointing to /tmp
VOLUME /tmp 

# Copy unpackaged app to new container
ARG DEPENDENCY=/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Execute the app
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.optimagrowth.gateway.ApiGatewayServerApplication"]
