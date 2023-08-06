FROM openjdk:8-jre-alpine

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app
RUN mkdir /var/activities
RUN chown -R $APPLICATION_USER /var/activities/
VOLUME /var/activities/

USER $APPLICATION_USER

COPY ./build/libs/activities.jar /app/activities.jar
COPY cert/jwt/* /app/activities/cert/jwt/
WORKDIR /app

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "activities.jar"]

EXPOSE 8080