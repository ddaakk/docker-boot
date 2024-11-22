# Docker Boot

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ddaakk/docker-boot.svg)](https://central.sonatype.com/artifact/io.github.ddaakk/docker-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

<p align="center">
  <a href="/README.md">English</a> |
  <strong>한국어</strong>
</p>

Docker Boot는 Docker 컨테이너와 Spring Boot 애플리케이션 간의 원활한 통합을 제공합니다. Spring 구성 및 프로그래밍 제어를 통해 Docker 컨테이너의 라이프사이클을 관리할 수 있습니다.

## 목차

- [특징](#특징)
- [시작하기](#시작하기)
   - [사전 준비](#사전-준비)
   - [설치](#설치)
   - [퀵 스타트](#퀵-스타트)
- [구성](#구성)
   - [기본 구성](#기본-구성)
   - [고급 구성](#고급-구성)
   - [라이프사이클 모드](#라이프사이클-모드)
   - [예제](#구성-예제)
- [이벤트 시스템](#이벤트-시스템)
- [Spring Boot Docker Compose와의 비교](#비교)
- [API 레퍼런스](#api-레퍼런스)
- [기여하기](#기여하기)
- [라이센스](#라이센스)

## 특징

- **Spring 네이티브 통합**
   - Spring Boot 자동 구성
   - YAML/Properties 기반 구성
   - Spring 프로파일 지원
   - Spring 이벤트 통합

- **컨테이너 라이프사이클 관리**
   - 세 가지 라이프사이클 모드 (START_AND_STOP, START_ONLY, NONE)
   - 자동 컨테이너 정리
   - 이벤트 기반 제어
   - 우아한 종료 처리

- **Docker 기능 지원**
   - 포트 매핑
   - 볼륨 마운트
   - 환경 변수
   - 헬스 체크
   - 리소스 제한
   - 커스텀 네트워크
   - 컨테이너 레이블

- **개발 지원**
   - 다중 컨테이너 관리
   - 개발/프로덕션 프로파일
   - 상세 로깅
   - 에러 처리

## 시작하기

### 사전 준비

- Java 17 이상
- Spring Boot 3.0 이상
- Docker Engine 설치 및 실행 중

### 설치

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

### 퀵 스타트

1. 프로젝트에 의존성을 추가합니다.
2. `application.yml`에서 컨테이너를 구성합니다:

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

3. Spring Boot 애플리케이션을 실행합니다.

## 구성

### 기본 구성

#### 전역 Docker 설정
```yaml
docker:
  host: unix:///var/run/docker.sock
  tls-verify: false
  registry:
    url: https://index.docker.io/v1/
    username: 사용자명
    password: 비밀번호
```

#### 컨테이너 설정
```yaml
docker:
  containers:
    서비스명:                        # 서비스 식별자
      enabled: true                  # 컨테이너 활성화/비활성화
      container-name: my-container   # 컨테이너 이름
      image-name: image:tag          # Docker 이미지
      lifecycle-mode: START_AND_STOP # 라이프사이클 모드
```

### 고급 구성

#### 전체 컨테이너 옵션
```yaml
docker:
  containers:
    서비스명:
      # 기본 설정
      enabled: true
      container-name: my-container
      image-name: image:tag
      lifecycle-mode: START_AND_STOP
      
      # 네트워크
      ports:
        8080: 8080
      networks:
        - network-name
      dns:
        - 8.8.8.8
      
      # 리소스
      memory: 512M
      cpu-shares: 1024
      
      # 스토리지
      volumes:
        /host/path: /container/path
      tmpfs:
        - /tmp
      
      # 환경 변수
      environment:
        KEY: value
      env-file:
        - ./env.list
      
      # 런타임
      command: ["custom", "command"]
      entrypoint: ["custom", "entrypoint"]
      working-dir: /app
      
      # 헬스 체크 및 모니터링
      healthcheck:
        test: ["CMD", "curl", "-f", "http://localhost"]
        interval: 30s
        timeout: 10s
        retries: 3
      
      # 추가 설정
      labels:
        app: service-name
      restart-policy:
        name: on-failure
        max-retry: 3
```

### 라이프사이클 모드

Docker Boot는 컨테이너 관리를 위한 세 가지 라이프사이클 모드를 제공합니다:

#### START_AND_STOP (기본값)
- Spring Boot 애플리케이션과 함께 시작
- 종료 시 컨테이너 중지 및 제거
- 개발 및 테스트에 최적

```yaml
lifecycle-mode: START_AND_STOP
```

#### START_ONLY
- Spring Boot 애플리케이션과 함께 시작
- 종료 후에도 계속 실행
- 공유 서비스에 적합

```yaml
lifecycle-mode: START_ONLY
```

#### NONE
- 자동 관리 없음
- 수동 제어만 가능
- 사전에 존재하는 컨테이너에 사용

```yaml
lifecycle-mode: NONE
```

### 구성 예제

#### 개발 데이터베이스
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

#### 프로덕션 캐시
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

## 이벤트 시스템

### 사용 가능한 이벤트

```java
public enum Action {
    START,   // 컨테이너 생성 및 시작
    STOP,    // 컨테이너 중지
    REMOVE   // 컨테이너 제거
}
```

### 이벤트 사용하기

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

### 이벤트 처리

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

## 비교

### Docker Boot vs Spring Boot Docker Compose

| 기능 | Docker Boot | Spring Boot Docker Compose |
|------|-------------|----------------------------|
| 구성 방식 | Spring YAML/Properties | docker-compose.yml |
| 컨테이너 제어 | 프로그래밍 + 이벤트 | 파일 기반 |
| Spring 통합 | 네이티브 통합 | 기본 통합 |
| 라이프사이클 모드 | 세 가지 모드 | 세 가지 모드 |
| 개발 초점 | 개발 및 프로덕션 모두 | 개발 중심 |

### Docker Boot를 선택해야 하는 경우

- 프로그래밍 방식의 컨테이너 제어가 필요한 경우
- Spring 네이티브 구성을 원하는 경우
- 이벤트 기반 관리가 필요한 경우
- 세밀한 라이프사이클 제어가 필요한 경우

## API 레퍼런스

### 핵심 인터페이스

#### DockerContainerManager
```java
public interface DockerContainerManager {
    String createAndStart();
    void stop(String containerId);
    void remove(String containerId);
}
```

#### 컨테이너 이벤트
```java
public class DockerContainerEvent extends ApplicationEvent {
    public enum Action {
        START, STOP, REMOVE
    }
}
```

## 기여하기

여러분의 기여를 환영합니다! 참여 방법은 다음과 같습니다:

1. 저장소를 포크합니다.
2. 기능 브랜치를 생성합니다 (`git checkout -b feature/amazing-feature`).
3. 변경 사항을 커밋합니다 (`git commit -m 'Add amazing feature'`).
4. 브랜치에 푸시합니다 (`git push origin feature/amazing-feature`).
5. Pull Request를 엽니다.

### 개발 환경 설정

1. 저장소를 클론합니다.
2. 의존성을 설치합니다.
3. 테스트를 실행합니다.
4. 변경 사항을 제출합니다.

## 라이센스

이 프로젝트는 Apache License 2.0에 따라 라이센스가 부여됩니다 - 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 작성자

ddaakk - [GitHub](https://github.com/ddaakk)

---

© 2024 Docker Boot. All rights reserved.
