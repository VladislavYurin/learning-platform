FROM maven:3.9.11-eclipse-temurin-21
WORKDIR /opt/app

COPY ./pom.xml ./
COPY ./common-lib/pom.xml ./common-lib/
COPY ./reactive-common-lib/pom.xml ./reactive-common-lib/
COPY ./api-gateway/pom.xml ./api-gateway/
COPY ./course-service/pom.xml ./course-service/
COPY ./mentor-service/pom.xml ./mentor-service/
COPY ./notification-service/pom.xml ./notification-service/
COPY ./calendar-service/pom.xml ./calendar-service/
COPY ./admin-service/pom.xml ./admin-service/

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl common-lib,reactive-common-lib dependency:go-offline

COPY ./common-lib/src ./common-lib/src
COPY ./reactive-common-lib/src ./reactive-common-lib/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl common-lib,reactive-common-lib install -DskipTests