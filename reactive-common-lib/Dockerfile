FROM maven:3.9.11-eclipse-temurin-21
WORKDIR /opt/app

COPY ./pom.xml ./
COPY ./common-lib/pom.xml ./common-lib/
COPY ./reactive-common-lib/pom.xml ./reactive-common-lib/
COPY ./grpc-common-starter/pom.xml ./grpc-common-starter/
COPY ./api-gateway/pom.xml ./api-gateway/
COPY ./course-service/pom.xml ./course-service/
COPY ./mentor-service/pom.xml ./mentor-service/
COPY ./notification-service/pom.xml ./notification-service/
COPY ./calendar-service/pom.xml ./calendar-service/
COPY ./admin-service/pom.xml ./admin-service/

RUN --mount=type=cache,target=/root/.m2,id=m2cache \
    mvn -B -ntp -pl common-lib,reactive-common-lib,grpc-common-starter dependency:go-offline

COPY ./common-lib/src ./common-lib/src
COPY ./reactive-common-lib/src ./reactive-common-lib/src
COPY ./grpc-common-starter/src ./grpc-common-starter/src

RUN --mount=type=cache,target=/root/.m2,id=m2cache \
    mvn -B -ntp -pl common-lib,reactive-common-lib,grpc-common-starter -am install -DskipTests