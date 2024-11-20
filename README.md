# Docker Boot

Docker Boot는 Spring Boot 애플리케이션에서 Docker 컨테이너를 쉽게 관리할 수 있게 해주는 라이브러리입니다.   
이 라이브러리를 사용하면 설정 파일만으로 Docker 컨테이너의 생명주기를 Spring 애플리케이션과 통합하여 관리할 수 있습니다.

## 특징
- Spring Boot 애플리케이션 시작/종료 시 자동으로 컨테이너 시작/종료
- YAML/Properties 파일을 통한 간단한 Docker 컨테이너 설정
- 다중 컨테이너 지원
- 자동 이미지 다운로드
- 포트 매핑, 볼륨 마운트, 환경 변수 설정 지원
- 컨테이너 생명주기 관리 자동화

## 시작하기

### 의존성 추가

Gradle:
```groovy
dependencies {
    implementation 'io.github.ddaakk:docker-container-spring-boot-starter:0.1.0'
}
```

Maven:
```xml
<dependency>
    <groupId>io.github.ddaakk</groupId>
    <artifactId>docker-container-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

### 기본 설정

application.yml:
```yaml
docker:
  host: unix:///var/run/docker.sock  # Docker 데몬 호스트
  tls-verify: false                  # TLS 검증 활성화 여부
  registry-url: https://index.docker.io/v1/  # Docker 레지스트리 URL
  
  # 컨테이너 설정
  containers:
    redis:  # 컨테이너 키
      enabled: true
      container-name: my-redis
      image-name: redis:latest
      ports:
        6379: 6379  # 호스트포트:컨테이너포트
      environment:
        REDIS_PASSWORD: mypassword
      volumes:
        /data/redis: /data  # 호스트경로:컨테이너경로

    mysql:
      enabled: true
      container-name: my-mysql
      image-name: mysql:8
      ports:
        3306: 3306
      environment:
        MYSQL_ROOT_PASSWORD: rootpass
        MYSQL_DATABASE: mydb
      volumes:
        /data/mysql: /var/lib/mysql
```

### 프로그래밍 방식 사용

```java
@Service
public class MyService {
    private final DockerContainerManager redisManager;
    
    public MyService(@Qualifier("redisContainerManager") DockerContainerManager redisManager) {
        this.redisManager = redisManager;
    }
    
    public void someMethod() {
        // 컨테이너 수동 제어가 필요한 경우
        String containerId = redisManager.createAndStart();
        // ... 컨테이너 사용 ...
        redisManager.stop(containerId);
        redisManager.remove(containerId);
    }
}
```

## 설정 옵션

### Docker 클라이언트 설정

| 속성 | 설명 | 기본값 |
|------|------|--------|
| docker.host | Docker 데몬 호스트 URL | unix:///var/run/docker.sock |
| docker.tls-verify | TLS 검증 활성화 여부 | false |
| docker.registry-url | Docker 레지스트리 URL | https://index.docker.io/v1/ |
| docker.registry-username | 레지스트리 사용자명 | |
| docker.registry-password | 레지스트리 비밀번호 | |

### 컨테이너 설정

| 속성 | 설명 | 필수 여부 |
|------|------|-----------|
| enabled | 컨테이너 활성화 여부 | false |
| container-name | 컨테이너 이름 | true |
| image-name | 이미지 이름 (태그 포함) | true |
| ports | 포트 매핑 (호스트:컨테이너) | false |
| environment | 환경 변수 | false |
| volumes | 볼륨 마운트 (호스트:컨테이너) | false |
| command | 컨테이너 실행 명령어 | false |
| entrypoint | 컨테이너 엔트리포인트 | false |
| labels | 컨테이너 레이블 | false |

## 동작 원리

1. Spring Boot 애플리케이션 시작 시:
   - 설정된 컨테이너별로 DockerContainerManager 빈이 생성됩니다.
   - 각 매니저는 SmartLifecycle 인터페이스를 구현하여 자동으로 컨테이너를 시작합니다.

2. 컨테이너 시작 프로세스:
   - 동일한 이름의 기존 컨테이너 제거
   - 이미지가 없는 경우 자동 다운로드
   - 컨테이너 생성 및 시작

3. 애플리케이션 종료 시:
   - 모든 컨테이너가 자동으로 정지 및 제거됩니다.

## 요구사항

- Java 17 이상
- Spring Boot 3.0 이상
- Docker Engine이 실행 중이어야 함

## 라이선스

Apache License 2.0

## 기여하기

버그 리포트, 기능 제안, PR은 언제나 환영합니다.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 작성자

ddaakk - [GitHub](https://github.com/ddaakk)
