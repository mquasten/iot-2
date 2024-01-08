# image wird in iot2.web verwendet
FROM ubuntu

RUN apt update

RUN apt install openjdk-21-jdk -y
RUN apt-get install -y iputils-ping
RUN apt-get install -y curl
RUN apt-get install -y vim
RUN apt-get install -y  tzdata

ENV DOWNLOAD https://repo1.maven.org/maven2/com/h2database/h2/2.1.214/h2-2.1.214.jar
ENV DATA_DIR /db
ENV H2_DIR /h2
ENV APP_DIR /iot2
ENV TZ Europe/Berlin

RUN mkdir ${APP_DIR}

RUN mkdir -p ${H2_DIR}

COPY target/iot2Batch.jar ${APP_DIR}/

RUN curl -L ${DOWNLOAD} -o ${H2_DIR}/h2-2.1.214.jar

#iot2.sh
RUN echo '#!/bin/bash' > ${APP_DIR}/iot2.sh
RUN echo "export CCU2=`ping -c1 homematic-ccu2 | grep 'from' |  awk ' {print $4 } '`" >> ${APP_DIR}/iot2.sh
RUN echo "export PATH=$PATH:${APP_DIR}" >> ${APP_DIR}/iot2.sh
RUN echo "java -jar ${H2_DIR}/h2-2.1.214.jar -webAllowOthers  -tcpAllowOthers -baseDir ${DATA_DIR}  &" >> ${APP_DIR}/iot2.sh
RUN echo 'if [ $1 == "!" ]; then' >> ${APP_DIR}/iot2.sh 
RUN echo 'CMD= `echo "$@" | sed "s/[!]//"`' >> ${APP_DIR}/iot2.sh
RUN echo 'bash -c "$CMD"' >> ${APP_DIR}/iot2.sh
RUN echo 'else'  >> ${APP_DIR}/iot2.sh
RUN echo 'java -jar iot2Batch.jar $@' >> ${APP_DIR}/iot2.sh
RUN echo 'fi' >> ${APP_DIR}/iot2.sh
RUN chmod +x ${APP_DIR}/iot2.sh

#endOfDay.sh
RUN echo '#!/bin/bash' > ${APP_DIR}/endOfDay.sh
RUN echo 'DATE=`date`' >>  ${APP_DIR}/endOfDay.sh
RUN echo 'echo start end-of-day at: $DATE' >>  ${APP_DIR}/endOfDay.sh
RUN echo 'java -jar iot2Batch.jar -c end-of-day' >>  ${APP_DIR}/endOfDay.sh
RUN echo 'echo start export-calendar at: $DATE' >>  ${APP_DIR}/endOfDay.sh 
RUN echo 'java -jar iot2Batch.jar -c export-calendar' /backup/calendar.csv >>  ${APP_DIR}/endOfDay.sh
RUN echo 'echo start export-configuration at: $DATE' >>  ${APP_DIR}/endOfDay.sh
RUN echo 'java -jar iot2Batch.jar -c export-configuration /backup/configuration.csv' >>  ${APP_DIR}/endOfDay.sh
RUN echo 'echo start export-protocol at: $DATE'  >>  ${APP_DIR}/endOfDay.sh 
RUN echo 'java -jar iot2Batch.jar -c export-protocol /backup/protocol.csv'  >>  ${APP_DIR}/endOfDay.sh 
RUN echo 'echo start cleanup-calendar at: $DATE' >>  ${APP_DIR}/endOfDay.sh 
RUN echo 'java -jar iot2Batch.jar -c cleanup-calendar' >>  ${APP_DIR}/endOfDay.sh 
RUN echo 'echo start cleanup-protocol at: $DATE' >>  ${APP_DIR}/endOfDay.sh
RUN echo 'java -jar iot2Batch.jar -c cleanup-protocol' >>  ${APP_DIR}/endOfDay.sh
RUN echo 'echo start db backup at: $DATE'  >>  ${APP_DIR}/endOfDay.sh
RUN echo "java -cp ${H2_DIR}/h2-2.1.214.jar  org.h2.tools.Script -url jdbc:h2:tcp://localhost/iot -user sa -password sa -script /backup/backup.sql" >>  ${APP_DIR}/endOfDay.sh

RUN chmod +x ${APP_DIR}/endOfDay.sh

WORKDIR ${APP_DIR}

ENTRYPOINT ["./iot2.sh"] 
CMD
