# Learning Platform Microservices

Система управления онлайн-курсами с распределенной архитектурой

## Архитектурная схема

```mermaid
graph TD
    A[Client] <-->|JWT Auth| B[API Gateway]
    B <-->|API Key| C[Course Service]
    B <-->|API Key| D[Mentor Service]
    C -->|Kafka| E[Notification Service]
    D -->|Kafka| E
    B --> F[(Database)]
    C --> F
    D --> F
    E --> F
    E --> G[Email]
    E --> H[Telegram]
```

## Состав системы

| Сервис                 | Назначение                                     |
|------------------------|------------------------------------------------|
| `api-gateway`          | Единая точка входа, авторизация, маршрутизация |
| `course-service`       | Управление курсами и модулями (CRUD, импорт)   |
| `mentor-service`       | Управление доступами, статистика прогресса     |
| `notification-service` | Отправка уведомлений (email, Telegram)         |
