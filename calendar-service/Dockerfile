FROM mentor/common-lib:local AS build
WORKDIR /opt/app

COPY ./pom.xml ./
COPY ./api-gateway/pom.xml ./api-gateway/
COPY ./course-service/pom.xml ./course-service/
COPY ./mentor-service/pom.xml ./mentor-service/
COPY ./notification-service/pom.xml ./notification-service/
COPY ./calendar-service/pom.xml ./calendar-service/
COPY ./admin-service/pom.xml ./admin-service/
COPY ./calendar-service/src ./calendar-service/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl calendar-service package -DskipTests

FROM eclipse-temurin:21-jre AS layers
WORKDIR /opt/app
COPY --from=build /opt/app/calendar-service/target/*.jar ./calendar-service.jar
RUN java -Djarmode=layertools -jar calendar-service.jar extract
RUN rm ./calendar-service.jar

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /opt/app
COPY --from=layers /opt/app/dependencies/ ./
COPY --from=layers /opt/app/spring-boot-loader/ ./
COPY --from=layers /opt/app/snapshot-dependencies/ ./
COPY --from=layers /opt/app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]