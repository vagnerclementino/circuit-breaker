FROM gradle:jdk11 AS cache
WORKDIR /app
ENV GRADLE_USER_HOME /cache
COPY build.gradle settings.gradle gradle.properties ./
RUN gradle getDeps --no-daemon --info --stacktrace

############################################################################

FROM gradle:jdk11 AS builder
WORKDIR /home/gradle
COPY --from=cache /cache /home/gradle/.gradle
COPY src/ src/
COPY build.gradle settings.gradle gradle.properties ./
RUN gradle bootJar

############################################################################
FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine
WORKDIR /app
# Copy bin from builder to this new image
COPY --from=builder /home/gradle/build/libs/*.jar app.jar

LABEL MAINTENER=vagner.clementino@gmail.com
EXPOSE 8090

ENV CONTAINER_PORT 8090

# Default command, run app
CMD java -jar /app/app.jar