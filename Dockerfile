FROM alpine/git as clone
WORKDIR /app
# Modify /template to your project name
# $GITHUB_SERVER_URL/$GITHUB_REPOSITORY
RUN git clone https://github.com/DGarbar/telegaBot

FROM maven:3.8.1-openjdk-17-slim AS build
WORKDIR /app
# Modify /template-java to your project name
COPY --from=clone /app/telegaBot /app
RUN mvn -Dmaven.test.skip=true clean package

FROM openjdk:17-oracle
WORKDIR /app
COPY --from=build /app/target/telegaBot-*.jar /app/telegaBot.jar
EXPOSE 8080
CMD ["java", "-jar", "telegaBot.jar"]

