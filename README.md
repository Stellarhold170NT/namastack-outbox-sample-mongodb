# Namastack Outbox MongoDB Sample

This is a sample application demonstrating the Outbox pattern using MongoDB.

## Prerequisites

1. Start MongoDB with Replica Set using Docker Compose:
docker-compose up -d

This command will start MongoDB and automatically initialize the required Replica Set (rs0).

## Configuration

The application is configured in src/main/resources/application.yml to connect to mongodb://localhost:27017/outbox_demo.

### Important Notes

1. **MongoDB Replica Set**: MongoDB **must** be configured as a Replica Set to support transactions, which are required for the Outbox pattern to ensure atomicity between business data and outbox events.
2. **Mandatory Annotation**: You must add `@EnableTransactionManagement` to your `@SpringBootApplication` class to ensure Spring correctly proxies your `@Transactional` services and detects the outbox transaction manager.

## Dependency Declaration

Add the following temporary dependency (for development) to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.stellarhold170nt</groupId>
    <artifactId>namastack-outbox-starter-mongodb</artifactId>
    <version>1.4.0-SNAPSHOT</version>
</dependency>
```

## Running the Application

Build and run using Maven:
mvn clean spring-boot:run

## Testing Endpoints

### 1. Create a successful order
This will save the order, publish an outbox event, and the scheduler will process it.

curl -X POST "http://localhost:8080/orders?customerId=user1&amount=100.0"

### 2. Create a failed order
This demonstrates the retry policy. The outbox handler is programmed to fail, and the scheduler will retry based on the exponential policy.

curl -X POST "http://localhost:8080/orders/failed?customerId=user1&amount=100.0"
