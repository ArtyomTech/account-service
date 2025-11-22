# Account Service

A Spring Boot application for managing accounts, balances, and transactions with event-driven architecture.

## Technologies

- Java 21, Spring Boot 3.5.7
- MyBatis, PostgreSQL 16, RabbitMQ
- Flyway migrations, MapStruct, Testcontainers

---

## Quick Start with Docker Compose

### Prerequisites

- Docker & Docker Compose
- Gradle (wrapper included, for building)

### Build and Run Everything

1. **Build the application:**

   ```bash
   ./gradlew clean bootJar
   ```

2. **Start all services (PostgreSQL, RabbitMQ, Application):**

   ```bash
   docker-compose up -d
   ```

   This will start:

   - PostgreSQL database
   - RabbitMQ message broker
   - Account Service application

3. **Access the services:**

   - **API**: `http://localhost:8080`
   - **RabbitMQ Management UI**: `http://localhost:15672`
     - Username: `guest`
     - Password: `guest`
   - **PostgreSQL Database**:
     - Host: `localhost`
     - Port: `5432`
     - Database: `account_service`
     - Username: `postgres`
     - Password: `postgres`

4. **View logs:**

   ```bash
   docker-compose logs -f account-service
   ```

5. **Stop all services:**

   ```bash
   docker-compose down
   ```

6. **Stop and remove all data:**

   ```bash
   docker-compose down -v
   ```

### Run Tests

```bash
./gradlew test
```

---

## Alternative: Run Locally (Development)

If you want to run the Spring Boot application locally for development:

1. **Start only dependencies:**

   ```bash
   docker-compose up -d postgres rabbitmq
   ```

2. **Run application locally:**

   ```bash
   ./gradlew bootRun
   ```

3. **Stop dependencies:**

   ```bash
   docker-compose stop
   ```

---

## What's Included in Docker Compose

- **postgres**: PostgreSQL 16 database
  - Port: `5432`
  - Database: `account_service`
  - Persistent volume: `postgres-data`
- **rabbitmq**: RabbitMQ 3 with management plugin
  - AMQP Port: `5672`
  - Management UI Port: `15672`
- **account-service**: Spring Boot application
  - Port: `8080`

Database schema is initialized automatically via Flyway migrations on application startup.

---

## Key Design Decisions

1. **MyBatis with XML mappers** - Better performance for complex queries with JOINs
2. **PostgreSQL enum types** - Database-level validation for currencies and transaction directions
3. **Event-driven with RabbitMQ** - Publishes events for all account/balance/transaction operations
4. **Testcontainers** - Integration tests use real PostgreSQL via Docker

---

## Performance Estimation

**Estimated TPS on development machine:** ~500-800 transactions/second

**Optimization potential:**

- Increase connection pool size
- Database replication
- Caching frequent lookups

---

## Horizontal Scaling Considerations

1. **Stateless design** - Multiple instances can run behind load balancer
2. **Database scaling** - Read replicas, partitioning by account_id
3. **Caching** - Redis for account/balance data to reduce DB load
4. **Monitoring** - Distributed tracing,metrics

---

## AI Usage

- Code generation (tests)
- Architecture suggestions (MyBatis XML)
- Performance estimation calculations and TPS breakdown
- Documentation
