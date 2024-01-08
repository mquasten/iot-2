FROM iot2batch

ENV APP_DIR /iot2

COPY target/iot2Web.jar ${APP_DIR}

#endOfDay.sh

RUN echo 'java -jar iot2Web.jar' > ${APP_DIR}/iotWeb.sh 
RUN chmod +x ${APP_DIR}/iotWeb.sh