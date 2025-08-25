ARG JAVA_IMAGE

FROM $JAVA_IMAGE

ARG VERSION
ARG NAME

WORKDIR /app

COPY target/$NAME-$VERSION.jar /app/application.jar

CMD java -jar application.jar