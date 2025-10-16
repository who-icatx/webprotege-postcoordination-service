FROM eclipse-temurin:17-jre-jammy
MAINTAINER protege.stanford.edu

ARG JAR_FILE
COPY target/${JAR_FILE} webprotege-postcoordination-service.jar
ENTRYPOINT ["java","-jar","/webprotege-postcoordination-service.jar"]