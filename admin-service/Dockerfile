FROM maven:3.9.11-eclipse-temurin-21 AS build
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

COPY ./common-lib/src ./common-lib/src
COPY ./reactive-common-lib/src ./reactive-common-lib/src
COPY ./grpc-common-starter/src ./grpc-common-starter/src
COPY ./admin-service/src ./admin-service/src

RUN mvn -B -ntp clean install -pl admin-service -am -DskipTests

FROM eclipse-temurin:21-jre AS layers
WORKDIR /opt/app
COPY --from=build /opt/app/admin-service/target/*.jar ./admin-service.jar
RUN java -Djarmode=layertools -jar admin-service.jar extract
RUN rm ./admin-service.jar

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /opt/app
COPY --from=layers /opt/app/dependencies/ ./
COPY --from=layers /opt/app/spring-boot-loader/ ./
COPY --from=layers /opt/app/snapshot-dependencies/ ./
COPY --from=layers /opt/app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]