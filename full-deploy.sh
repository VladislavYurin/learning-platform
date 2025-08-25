# --- 1. Очистка старых образов ---
echo "🛑 Удаление старых образов mentor/..."
./build.sh

# --- 2. Запуск инфраструктуры ---
echo "🛢️ Запуск PostgreSQL и Kafka..."
./run-infra.sh &

# Ждём, пока инфраструктура поднимется
echo "⏳ Ожидание старта инфраструктуры (1 мин)..."
sleep 60

# --- 3. Запуск микросервисов ---
echo "🚀 Запуск микросервисов..."
docker-compose up -d \
  api-gateway \
  course-service \
  mentor-service \
  notification-service \
  calendar-service \
  admin-service

# --- 4. Проверка статуса ---
echo "✅ Готово! Статус контейнеров:"
docker-compose ps