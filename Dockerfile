FROM alpine:3.9

ARG quarkusVersion
ARG appVersion

RUN apk add --no-cache openjdk8-jre-base && mkdir -p /opt/quarkus/lib
RUN echo $quarkusVersion >/opt/quarkus/quarkusVersion.txt

ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV AB_ENABLED=jmx_exporter

ENV AUTODEPLOY_FOLDER "/opt/quarkus"

COPY target/lib/* ${AUTODEPLOY_FOLDER}/lib/
COPY target/movies-backend-${appVersion}-runner.jar ${AUTODEPLOY_FOLDER}/app.jar

WORKDIR /opt/quarkus
RUN echo $appVersion >/opt/quarkus/appVersion.txt
CMD java -Xmx256m -Xms128m app.jar
