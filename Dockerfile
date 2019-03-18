FROM openjdk:11-jre-slim AS builder
WORKDIR /root
COPY target target
COPY conf conf
COPY bin bin
EXPOSE 8080 8081
ENTRYPOINT ["./bin/entrypoint.sh"]