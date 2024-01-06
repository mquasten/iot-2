FROM ubuntu

RUN apt update

RUN apt install openjdk-21-jdk -y
RUN apt-get install -y iputils-ping
RUN apt-get install -y curl
RUN apt-get install -y unzip


ENV DOWNLOAD https://repo1.maven.org/maven2/com/h2database/h2/2.1.214/h2-2.1.214.jar
ENV DATA_DIR /db
ENV H2_DIR /h2
ENV APP_DIR /iot2



RUN mkdir ${APP_DIR}
RUN mkdir ${DATA_DIR}cat

RUN mkdir -p ${H2_DIR}

COPY target/iot2Batch.jar ${APP_DIR}/

#RUN echo '192.168.2.100 homematic-ccu2' >> /etc/hosts

RUN curl -L ${DOWNLOAD} -o ${H2_DIR}/h2-2.1.214.jar
RUN echo '#!/bin/bash' > ${APP_DIR}/iot2.sh
RUN echo "java -jar ${H2_DIR}/h2-2.1.214.jar -webAllowOthers  -tcpAllowOthers -baseDir ${DATA_DIR} &" >> ${APP_DIR}/iot2.sh

RUN echo -n "java -D CCU2_HOST=$CCU2_HOST -jar iot2Batch.jar " >> ${APP_DIR}/iot2.sh

RUN echo '$@' >>  ${APP_DIR}/iot2.sh

RUN chmod +x ${APP_DIR}/iot2.sh

WORKDIR ${APP_DIR}
RUN 

#ENTRYPOINT ["./iot2.sh"] 
CMD
