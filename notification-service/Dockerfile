FROM mentor/common-lib:local AS build
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
COPY ./notification-service/src ./notification-service/src

RUN --mount=type=cache,target=/root/.m2,id=m2cache \
    mvn -B -ntp -pl notification-service package -DskipTests

FROM eclipse-temurin:21-jre AS layers
WORKDIR /opt/app
COPY --from=build /opt/app/notification-service/target/*.jar ./notification-service.jar
RUN java -Djarmode=layertools -jar notification-service.jar extract
RUN rm ./notification-service.jar

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /opt/app
COPY --from=layers /opt/app/dependencies/ ./
COPY --from=layers /opt/app/spring-boot-loader/ ./
COPY --from=layers /opt/app/snapshot-dependencies/ ./
COPY --from=layers /opt/app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]