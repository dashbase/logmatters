FROM openjdk:11-jdk-slim AS builder
WORKDIR /root
RUN apt update && apt install maven -y && mvn --version

COPY src src
COPY pom.xml pom.xml
RUN mvn clean package
COPY conf conf
COPY bin bin


EXPOSE 8080 8081
ENTRYPOINT ["./bin/entrypoint.sh"]
