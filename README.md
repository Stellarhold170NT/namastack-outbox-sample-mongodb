# Outbox Pattern Sample with MongoDB (Custom Implementation)

This is a sample application demonstrating the Outbox pattern using a custom implementation with MongoDB.

## Prerequisites

1. Start MongoDB with Replica Set using Docker Compose:
docker-compose up -d

This command will start MongoDB and automatically initialize the required Replica Set (rs0).

## Configuration

The application is configured in src/main/resources/application.yml to connect to mongodb://localhost:27017/outbox_demo.

### Important Notes

1. **MongoDB Replica Set**: MongoDB **must** be configured as a Replica Set to support transactions, which are required for the Outbox pattern to ensure atomicity between business data and outbox events.

## Running the Application

Build and run using Maven:
```bash
mvn clean spring-boot:run
```

## Testing Endpoints

### 1. Create a successful order

```bash
curl -X POST "http://localhost:8080/orders?customerId=user1&amount=100.0"
```

### 2. Create a failed order (demonstrates retry)

```bash
curl -X POST "http://localhost:8080/orders/failed?customerId=user1&amount=100.0"
```
