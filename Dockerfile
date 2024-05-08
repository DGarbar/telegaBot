FROM maven:3.8.1-openjdk-17-slim AS build
WORKDIR /app
COPY ./target/telegabot-*.jar /app/telegabot.jar
CMD java $JAVA_OPTS -jar /app/telegabot.jar
