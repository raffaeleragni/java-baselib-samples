FROM adoptopenjdk/openjdk15-openj9:alpine

RUN mkdir /app && mkdir /app/lib
WORKDIR /app

EXPOSE 8080
HEALTHCHECK CMD curl -k --fail https://localhost:8080 || exit 1
CMD java --enable-preview \
  -Xmx8m \
  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:9999\
  -jar app.jar

COPY target/lib/* /app/lib/

COPY target/*.jar /app/app.jar

ARG APP_VERSION
ENV APP_VERSION ${APP_VERSION}
RUN echo ${APP_VERSION} > version
