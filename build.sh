echo "Удаление старых образов mentor/..."
docker rmi -f $(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'mentor/')

echo "Сборка jarников..."
mvn clean package -DskipTests

echo "Сборка Docker-образов..."
docker-compose build