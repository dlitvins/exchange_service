# Exchange Service

## Prerequisites

- JDK 21
- Docker

## Build and Run

### 1. Build project

```bash
./gradlew build
```

### 2. Running Tests

To run the test suite, use the following command:

```bash
./gradlew clean test
```

### 3. Run the Application outside container

- Pull Images:

```bash
docker-compose -f ./compose.yaml pull database
```

- Start Docker Container for database:

```bash
docker-compose -f ./compose.yaml up -d
```

- and use the `bootRun` task:

```bash
./gradlew bootRun --args='--spring.profiles.active=default,local' 
```

This command will start the Spring Boot application using the default and local profiles.

In order to stop database container:

```bash
docker-compose -f ./compose.yaml down --remove-orphans
```

### 4. Run the Application inside container

- Pull Images:

```bash
docker-compose -f ./compose.yaml pull database
```

- Building the Docker Image:

```bash
./gradlew bootBuildImage
 ```

- Start Docker Container for application and database:

```bash
docker-compose -f ./compose.yaml --profile with_backend up -d
 ```

In order to totally stop container:

```bash
docker-compose -f ./compose.yaml --profile with_backend down --remove-orphans
```

### 5. Open Application

Visit the [Landing page of Exchange Service](http://localhost:8080)