FROM adoptopenjdk/openjdk16-openj9:alpine

RUN mkdir /app && mkdir /app/lib
WORKDIR /app

EXPOSE 8080
CMD java -jar app.jar

COPY target/lib/* /app/lib/
COPY target/*.jar /app/app.jar

ARG APP_VERSION
ENV APP_VERSION ${APP_VERSION}
RUN echo ${APP_VERSION} > version
