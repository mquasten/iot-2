FROM ubuntu

RUN apt update

RUN apt install openjdk-21-jdk -y
RUN apt-get install -y iputils-ping
RUN apt-get install -y curl
RUN apt-get install -y unzip

ENV DOWNLOAD https://github.com/h2database/h2database/releases/download/version-2.1.214/h2-2022-06-13.zip

RUN mkdir /iot2
RUN mkdir /db
RUN mkdir -p /h2 \
    && curl -L ${DOWNLOAD} -o h2.zip \
    && unzip h2.zip -d /h2/ \
    && rm h2.zip

COPY target/iot2Web.jar /iot2
RUN cp /h2/h2/bin/h2-2.1.214.jar /h2/
RUN rm -rf /h2/h2

RUN echo "java -jar /h2/h2-2.1.214.jar  -baseDir /db & java -jar iot2Web.jar" > /iot2/iot2.sh
RUN chmod +x /iot2/iot2.sh

WORKDIR /iot2

CMD ["sh", "-c" , "/iot2/iot2.sh"]
