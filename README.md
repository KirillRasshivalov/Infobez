# ProtectedApi

Защищённый REST API (Java, Spring Boot, PostgreSQL) лабораторная работа №1.

## API

Базовый URL: `http://localhost:8080`

### 1. `POST /auth/register`

Регистрация пользователя.

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"secret12"}'
```

Ответ `201`:

```json
{"token":"<JWT>","username":"user1"}
```

### 2. `POST /auth/login`

Аутентификация. При успехе возвращает JWT.

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"secret12"}'
```

Ответ `200`:

```json
{"token":"<JWT>","username":"user1"}
```

### 3. `GET /api/data`

Список пользователей. Доступ только с валидным JWT.

```bash
curl http://localhost:8080/api/data \
  -H "Authorization: Bearer <JWT>"
```

Ответ `200`:

```json
[{"id":1,"username":"user1"}]
```

Без токена — `401 Unauthorized`.

## Меры защиты

### SQL Injection

Доступ к БД через Spring Data JPA (`UserRepository`). Запросы формируются ORM как параметризованные (Prepared Statements), без конкатенации строк в SQL.

### XSS

Пользовательские данные в ответах API экранируются через `HtmlUtils.htmlEscape` (`XssSanitizer`) перед возвратом клиенту (`username` в `/auth/*` и `/api/data`).

### Аутентификация (Broken Authentication)

- Пароли хранятся только как bcrypt-хэш (`BCryptPasswordEncoder`), не в открытом виде.
- После успешного login/register выдаётся JWT (HMAC).
- `JwtAuthFilter` проверяет Bearer-токен на защищённых эндпоинтах; `/auth/login` и `/auth/register` открыты, остальные требуют аутентификации.

## CI/CD (SAST / SCA)

При push/PR в GitHub Actions запускаются:

- **SAST:** SpotBugs → `spotbugsSarif.json`
- **SCA:** OWASP Dependency-Check → `dependency-check-report.json`

Отчёты сохраняются как artifact `security-reports`.

### Скриншоты отчётов из Actions

![SAST SpotBugs](docs/sast-spotbugs.png)

![SCA Dependency-Check](docs/sca-dependency-check.png)
