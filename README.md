# 🌐 SocialNet Backend

![Java](https://img.shields.io/badge/Java-22-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Pub%2FSub-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Keycloak](https://img.shields.io/badge/Keycloak-OAuth2-add8e6?style=for-the-badge&logo=keycloak&logoColor=white)
![LiveKit](https://img.shields.io/badge/LiveKit-Streaming-00D2FF?style=for-the-badge&logo=webrtc&logoColor=white)

Мощный и масштабируемый RESTful API для современной социальной сети. Проект реализует микросервисный подход внутри монолита (Modular Monolith) с использованием гексагональной архитектуры. Поддерживает полный цикл социального взаимодействия: от постов и умной ленты до видеозвонков в реальном времени.

---

## 🚀 Ключевые Возможности

### 🔐 Авторизация и Безопасность (`AuthController`)
- **OAuth2 / OpenID Connect:** Интеграция с **Keycloak** для управления пользователями.
- **JWT Authentication:** Stateless аутентификация через токены доступа.
- **Flow:** Регистрация, Вход, Рефреш токенов, Смена пароля, Логаут.

### 📝 Контент и Лента (`PostController`)
- **Мультимедиа посты:** Текст, Изображения, Видео, Опросы.
- **Интерактивы:** Лайки, комментарии, репосты.
- **Умная лента:** Пагинация, фильтрация по типу контента и авторам.
- **Тренды:** Алгоритм вычисления популярных тем (хештегов) за 24 часа.

### 💬 Мессенджер в реальном времени (`ConversationController`, `MessageController`)
- **WebSockets (STOMP):** Мгновенная доставка сообщений.
- **Redis Pub/Sub:** Синхронизация статусов "Онлайн" между инстансами и пользователями.
- **Диалоги:** Приватные и групповые чаты, история сообщений, поиск по переписке (включая контекстный поиск сообщений "вокруг" найденного).
- **Уведомления:** Push-уведомления о новых сообщениях через сокеты.

### 📞 Видео и Аудио Звонки (`CallController`, `SignalingController`)
- **LiveKit Integration:** Генерация токенов для WebRTC соединений.
- **Signaling:** Обмен сигналами начала звонка через WebSocket.
- **Уведомления о звонках:** Всплывающие уведомления о входящем вызове.

### 👤 Профиль и Социальный Граф (`UserController`)
- **Профиль:** Редактирование био, загрузка аватара (хранение на диске).
- **Друзья:** Система заявок (отправка, ожидание, прием, отклонение), черные списки.
- **Поиск:** Поиск людей по имени.
- **Настройки:** Управление приватностью (кто видит посты), уведомлениями и темой оформления.
- **Onboarding:** Трекинг этапов приветствия новых пользователей.

---

## 🛠️ Технологический Стек

Проект построен на **Java 22** с использованием передовых практик Spring Boot 3.5.0.

| Область | Технологии |
|---------|------------|
| **Core** | Spring Boot 3.5.0 (Web, Validation, Actuator, AOP) |
| **Database** | PostgreSQL + Flyway (миграции V1__init.sql) |
| **ORM** | Spring Data JPA + Hibernate + Hypersistence Utils |
| **Caching & Messaging** | Redis (Cache, Pub/Sub для WebSockets) |
| **Security** | Spring Security 6, OAuth2 Resource Server, JWT |
| **Real-time** | WebSocket (STOMP), LiveKit (WebRTC) |
| **Observability** | Sentry (Error Tracking), Spring Boot Actuator |
| **Utils** | Lombok, MapStruct (DTO mapping), Jackson |
| **Documentation** | Swagger UI (OpenAPI 3) |

---

## ⚙️ Конфигурация и Запуск

### 1. Предварительные требования
*   **Java 22 JDK**
*   **Docker & Docker Compose** (для БД, Redis, Keycloak, LiveKit)
*   **Maven** (встроен wrapper `./mvnw`)

### 2. Настройка окружения (.env)
Создайте файл `.env` в корне проекта (или используйте переменные среды Docker Compose).

```properties
# === Database ===
POSTGRES_USER=postgres
POSTGRES_PASSWORD=YOUR_PASSWORD_HERE
POSTGRES_DB=socialnet
POSTGRES_DB_AUTH=socialnet_auth
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/socialnet

# === Redis ===
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=32768

# === Keycloak (Auth) ===
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_AUTH_SERVER_URL=http://localhost:8040
KEYCLOAK_REALM=socialnet
KEYCLOAK_CLIENT_ID=socialnet
KEYCLOAK_CLIENT_SECRET=YOUR_SECRET_HERE
# Внутренний URL для общения контейнеров
KEYCLOAK_AUTH_SERVER_URL_PRIVATE=http://keycloak:8080

# === LiveKit (Video Calls) ===
LIVEKIT_API_KEY=devkey
LIVEKIT_API_SECRET=mySuperSecretKeyMustBeAtLeast32CharsLong
LIVEKIT_URL=http://localhost:7880
```

### 3. Запуск через Docker (Рекомендуемый)

```bash
# Сборка и запуск всех сервисов
docker-compose up -d --build
```

### 4. Локальный запуск (Development)

Если вы запускаете базы данных в Docker, а приложение локально:

1.  Убедитесь, что сервисы (Postgres, Redis, Keycloak) запущены.
2.  Выполните команду:
    ```bash
    ./mvnw spring-boot:run
    ```

---

## 📖 Документация API

После запуска сервера Swagger UI доступен по адресу:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Здесь можно протестировать все эндпоинты, авторизовавшись через кнопку `Authorize` (Bearer Token).

---

## 📂 Архитектура (Hexagonal / DDD)

Структура пакетов разделена по бизнес-модулям:

```
src/main/java/org/socialnet/socialnet/
├── auth/                   # Модуль авторизации (Port: SecurityPortUseCase)
├── message/                # Модуль чатов и звонков
│   ├── application/web/    # WebSocket & REST контроллеры
│   ├── core/               # Доменная логика
│   └── infrastructure/     # Работа с БД
├── post/                   # Модуль постов и ленты
├── user/                   # Модуль профилей и друзей
└── shared/                 # Общие конфигурации (Security, Redis, Swagger)
```

---

<div align="center">
    <sub>Made with ❤️ by Владислав Модж</sub>
</div>