FROM openjdk:11.0.1-stretch
WORKDIR /root
COPY target target
COPY conf conf
COPY bin bin
EXPOSE 8080 8081
ENTRYPOINT ["./bin/entrypoint.sh"]
