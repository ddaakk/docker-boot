# Docker Boot

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ddaakk/docker-boot.svg)](https://central.sonatype.com/artifact/io.github.ddaakk/docker-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

<p align="center">
  <strong>English</strong> |
  <a href="/README_ko.md">한국어</a>
</p>

Docker Boot provides seamless integration between Docker containers and Spring Boot applications. It allows you to manage Docker container lifecycles through Spring configuration and programmatic controls.

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Installation](#installation)
   - [Quick Start](#quick-start)
- [Configuration](#configuration)
   - [Basic Configuration](#basic-configuration)
   - [Advanced Configuration](#advanced-configuration)
   - [Lifecycle Modes](#lifecycle-modes)
   - [Examples](#configuration-examples)
- [Event System](#event-system)
- [Comparison with Spring Boot Docker Compose](#comparison)
- [API Reference](#api-reference)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Spring Native Integration**
   - Spring Boot auto-configuration
   - YAML/Properties based configuration
   - Spring profiles support
   - Spring events integration

- **Container Lifecycle Management**
   - Three lifecycle modes (START_AND_STOP, START_ONLY, NONE)
   - Automatic container cleanup
   - Event-driven control
   - Graceful shutdown handling

- **Docker Features Support**
   - Port mapping
   - Volume mounting
   - Environment variables
   - Health checks
   - Resource constraints
   - Custom networks
   - Container labels

- **Development Support**
   - Multi-container management
   - Development/Production profiles
   - Detailed logging
   - Error handling

## Getting Started

### Prerequisites

- Java 17 or higher
- Spring Boot 3.0 or higher
- Docker Engine installed and running

### Installation

#### Maven
```xml
<dependency>
    <groupId>io.github.ddaakk</groupId>
    <artifactId>docker-container-spring-boot-starter</artifactId>
    <version>0.2.0</version>
</dependency>
```

#### Gradle
```groovy
implementation 'io.github.ddaakk:docker-container-spring-boot-starter:0.2.0'
```

### Quick Start

1. Add the dependency to your project
2. Configure containers in `application.yml`:

```yaml
docker:
  containers:
    redis:
      enabled: true
      container-name: my-redis
      image-name: redis:latest
      lifecycle-mode: START_AND_STOP
      ports:
        6379: 6379
```

3. Run your Spring Boot application

## Configuration

### Basic Configuration

#### Global Docker Settings
```yaml
docker:
  host: unix:///var/run/docker.sock
  tls-verify: false
  registry:
    url: https://index.docker.io/v1/
    username: username
    password: password
```

#### Container Settings
```yaml
docker:
  containers:
    service-name:                    # Service identifier
      enabled: true                  # Enable/disable container
      container-name: my-container   # Container name
      image-name: image:tag         # Docker image
      lifecycle-mode: START_AND_STOP # Lifecycle mode
```

### Advanced Configuration

#### Full Container Options
```yaml
docker:
  containers:
    service-name:
      # Basic Settings
      enabled: true
      container-name: my-container
      image-name: image:tag
      lifecycle-mode: START_AND_STOP
      
      # Network
      ports:
        8080: 8080
      networks:
        - network-name
      dns:
        - 8.8.8.8
      
      # Resources
      memory: 512M
      cpu-shares: 1024
      
      # Storage
      volumes:
        /host/path: /container/path
      tmpfs:
        - /tmp
      
      # Environment
      environment:
        KEY: value
      env-file:
        - ./env.list
      
      # Runtime
      command: ["custom", "command"]
      entrypoint: ["custom", "entrypoint"]
      working-dir: /app
      
      # Health & Monitoring
      healthcheck:
        test: ["CMD", "curl", "-f", "http://localhost"]
        interval: 30s
        timeout: 10s
        retries: 3
      
      # Additional Settings
      labels:
        app: service-name
      restart-policy:
        name: on-failure
        max-retry: 3
```

### Lifecycle Modes

Docker Boot provides three lifecycle modes for container management:

#### START_AND_STOP (Default)
- Starts with Spring Boot application
- Stops and removes on shutdown
- Best for development and testing

```yaml
lifecycle-mode: START_AND_STOP
```

#### START_ONLY
- Starts with Spring Boot application
- Continues running after shutdown
- Good for shared services

```yaml
lifecycle-mode: START_ONLY
```

#### NONE
- No automatic management
- Manual control only
- For pre-existing containers

```yaml
lifecycle-mode: NONE
```

### Configuration Examples

#### Development Database
```yaml
docker:
  containers:
    postgres:
      enabled: true
      container-name: postgres-dev
      image-name: postgres:14
      lifecycle-mode: START_AND_STOP
      ports:
        5432: 5432
      environment:
        POSTGRES_DB: devdb
        POSTGRES_USER: dev
        POSTGRES_PASSWORD: devpass
      volumes:
        postgres-data: /var/lib/postgresql/data
```

#### Production Cache
```yaml
docker:
  containers:
    redis:
      enabled: true
      container-name: redis-prod
      image-name: redis:7
      lifecycle-mode: START_ONLY
      ports:
        6379: 6379
      memory: 1G
      healthcheck:
        test: ["CMD", "redis-cli", "ping"]
        interval: 10s
```

## Event System

### Available Events

```java
public enum Action {
    START,   // Create and start container
    STOP,    // Stop container
    REMOVE   // Remove container
}
```

### Using Events

```java
@Service
public class DockerService {
    private final ApplicationEventPublisher eventPublisher;
    
    public void startContainer() {
        eventPublisher.publishEvent(
            new DockerContainerEvent(this, DockerContainerEvent.Action.START)
        );
    }
}
```

### Event Handling

```java
@Service
public class ContainerManager {
    @EventListener
    public void handleDockerEvent(DockerContainerEvent event) {
        switch (event.getAction()) {
            case START -> startContainer();
            case STOP -> stopContainer();
            case REMOVE -> removeContainer();
        }
    }
}
```

## Comparison

### Docker Boot vs Spring Boot Docker Compose

| Feature | Docker Boot | Spring Boot Docker Compose |
|---------|------------|---------------------------|
| Configuration | Spring YAML/Properties | docker-compose.yml |
| Container Control | Programmatic + Events | File-based |
| Spring Integration | Native integration | Basic integration |
| Lifecycle Modes | Three modes | Three modes |
| Development Focus | Both dev and prod | Development focused |

### When to Choose Docker Boot

- Need programmatic container control
- Want Spring-native configuration
- Require event-based management
- Need fine-grained lifecycle control

## API Reference

### Core Interfaces

#### DockerContainerManager
```java
public interface DockerContainerManager {
    String createAndStart();
    void stop(String containerId);
    void remove(String containerId);
}
```

#### Container Events
```java
public class DockerContainerEvent extends ApplicationEvent {
    public enum Action {
        START, STOP, REMOVE
    }
}
```

## Contributing

We welcome contributions! Here's how you can help:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Setup

1. Clone the repository
2. Install dependencies
3. Run tests
4. Submit changes

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Author

ddaakk - [GitHub](https://github.com/ddaakk)

---

© 2024 Docker Boot. All rights reserved.
