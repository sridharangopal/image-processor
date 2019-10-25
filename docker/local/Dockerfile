FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=../../build/libs/app.jar
ARG JACOCO_JAR=../../codecov/jacocoagent.jar
COPY ${JACOCO_JAR} jacocoagent.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","-javaagent","jacocoagent.jar=port=6300,address=jacoco-server,output=tcpclient"]
