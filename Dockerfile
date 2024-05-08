FROM maven:3.8.1-openjdk-17-slim AS build
WORKDIR /app
COPY ./target/telegabot-*.jar /app/telegabot.jar
ENTRYPOINT ["tail", "-f", "/dev/null"]

CMD java -Dserver.port=$PORT $JAVA_OPTS -jar /app/telegabot.jar
