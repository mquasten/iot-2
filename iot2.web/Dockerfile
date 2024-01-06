FROM openjdk:21




ENV DOWNLOAD https://repo1.maven.org/maven2/com/h2database/h2/2.1.214/h2-2.1.214.jar
ENV DATA_DIR /db
ENV H2_DIR /h2
ENV APP_DIR /iot2


RUN mkdir ${APP_DIR}
RUN mkdir ${DATA_DIR}

RUN mkdir -p ${H2_DIR}

COPY target/iot2Web.jar ${APP_DIR}/


RUN curl -L ${DOWNLOAD} -o ${H2_DIR}/h2-2.1.214.jar

RUN echo "java -jar ${H2_DIR}/h2-2.1.214.jar -webAllowOthers  -tcpAllowOthers -baseDir ${DATA_DIR} & java -jar ${APP_DIR}/iot2Web.jar" > ${APP_DIR}/iot2.sh

RUN chmod +x ${APP_DIR}/iot2.sh

WORKDIR ${APP_DIR}

CMD ["sh", "-c" , "${APP_DIR}/iot2.sh"]
