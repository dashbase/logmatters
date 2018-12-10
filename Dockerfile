FROM openjdk:11-jre
WORKDIR /opt/logmatters

RUN curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-6.5.0-amd64.deb && \
    dpkg -i filebeat-6.5.0-amd64.deb

RUN curl -L -O https://dl.influxdata.com/telegraf/releases/telegraf_1.9.0-1_amd64.deb && \
    dpkg -i telegraf_1.9.0-1_amd64.deb

RUN apt update && apt install -y vim dstat htop supervisor

ADD ./target /opt/logmatters
