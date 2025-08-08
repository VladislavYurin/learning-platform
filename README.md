# Learning Platform Microservices

Система управления онлайн-курсами с распределенной архитектурой

## Архитектурная схема

```mermaid
graph TD
    A[Client] <-->|JWT Auth| B[API Gateway]
    B <-->|API Key| C[Course Service]
    B <-->|API Key| D[Mentor Service]
    B <-->|API Key| M[Calendar Service]
    C -->|Kafka| E[Notification Service]
    D -->|Kafka| E
    M -->|Kafka| E
    B --> F[(Database)]
    C --> F
    D --> F
    E --> F
    M --> F
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
| `calendar-service`     | Управление календарем, букинг                  |

## Swagger API Gateway

http://localhost:8080/swagger-ui/index.html#/

## Insomnia

[Insomnia_endpoints.yaml](Insomnia_endpoints.yaml)

Импортировать в свою локальную инсомнию. 

При добавлении новых ендпоинтов в api-gateway также добавить их сюда.