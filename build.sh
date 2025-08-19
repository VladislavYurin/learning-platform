docker rmi -f $(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'mentor/')
mvn clean package -P docker-build $1 -DskipTests