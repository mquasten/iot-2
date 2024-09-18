# Testdatenbank 
FROM openjdk:23

ENV DOWNLOAD https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar
ENV DATA_DIR /db
ENV H2_DIR /h2
RUN mkdir -p ${H2_DIR}
WORKDIR ${H2_DIR}

RUN curl -L ${DOWNLOAD} -o h2-2.2.224.jar
    
EXPOSE 8082 9092     

HEALTHCHECK CMD curl --http0.9  --fail --output /dev/null  http://localhost:9092 || exit 1 

CMD java -cp h2*.jar org.h2.tools.Server \
    -web -webAllowOthers \
    -tcp -tcpAllowOthers \
    -baseDir ${DATA_DIR} 