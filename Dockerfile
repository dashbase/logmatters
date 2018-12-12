FROM openjdk:11-jdk-slim AS builder
WORKDIR /root
RUN apt update && apt install maven -y && mvn --version

COPY . .
RUN mvn clean package

EXPOSE 8080 8081
ENTRYPOINT ["./bin/entrypoint.sh"]
