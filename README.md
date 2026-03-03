# 팀 프로젝트 모집 플랫폼 - 백엔드

Spring Boot 기반의 대학생 팀 프로젝트 모집 플랫폼 백엔드 API

## 주요 기능

### 1. 세종대학교 학사정보시스템 연동 로그인
- 학번 + 비밀번호로 세종대 포털 인증
- 로그인 시 학생 정보(이름, 학과, 학년) 자동 조회 및 저장
- 별도의 회원가입 절차 없음

### 2. JWT 기반 인증
- Access Token (24시간)
- Refresh Token (7일)
- 자동 토큰 갱신

### 3. 모집 공고 관리
- 공고 작성/수정/삭제
- 프로젝트 타입별 필터링 (창의학기제, 캡스톤, 해커톤 등)
- 기술 스택별 검색


### 4. 지원 관리
- 공고 지원/취소
- 지원자 관리 (수락/거절)
- 내 지원 내역 조회

## 프로젝트 구조

```
src/main/java/com/sejong/recruit/
├── config/              # 설정 파일
│   └── SecurityConfig.java
├── controller/          # REST API 컨트롤러
│   ├── AuthController.java
│   ├── RecruitController.java
│   ├── ApplicationController.java
│   └── UserController.java
├── dto/                 # 데이터 전송 객체
│   ├── AuthDto.java
│   ├── RecruitDto.java
│   ├── ApplicationDto.java
│   └── UserDto.java
├── entity/              # JPA 엔티티
│   ├── User.java
│   ├── RecruitPost.java
│   └── Application.java
├── repository/          # JPA Repository
│   ├── UserRepository.java
│   ├── RecruitPostRepository.java
│   └── ApplicationRepository.java
├── service/             # 비즈니스 로직
│   ├── AuthService.java
│   ├── SejongPortalService.java
│   ├── RecruitService.java
│   ├── ApplicationService.java
│   └── UserService.java
├── security/            # 보안 관련
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── util/                # 유틸리티
│   └── JwtUtil.java
├── exception/           # 예외 처리
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
└── TeamRecruitPlatformApplication.java
```

## 실행 방법

### 1. 사전 요구사항
- JDK 17 이상
- Maven 3.6 이상

### 2. 실행

```bash
# Maven을 사용하여 실행
./mvnw spring-boot:run

# 또는 IntelliJ에서
# TeamRecruitPlatformApplication.java 우클릭 -> Run
```

### 3. 접속
- API 서버: http://localhost:8080/api
- H2 Console: http://localhost:8080/api/h2-console

## API 엔드포인트

### 인증 (Authentication)
```
POST   /auth/login      # 로그인
POST   /auth/refresh    # 토큰 갱신
GET    /auth/me         # 내 정보 조회
POST   /auth/logout     # 로그아웃
```

### 모집 공고 (Recruits)
```
GET    /recruits                    # 공고 목록 조회
GET    /recruits/{id}               # 공고 상세 조회
POST   /recruits                    # 공고 작성
PUT    /recruits/{id}               # 공고 수정
DELETE /recruits/{id}               # 공고 삭제
```

### 지원 (Applications)
```
POST   /recruits/{id}/applications              # 지원하기
GET    /recruits/{id}/applications              # 지원자 목록 (작성자용)
PATCH  /recruits/{id}/applications/{appId}      # 지원 수락/거절
DELETE /recruits/{id}/applications/{appId}      # 지원 취소
```

### 사용자 (Users)
```
GET    /users/me                 # 내 프로필 조회
GET    /users/{id}               # 사용자 프로필 조회
PUT    /users/me                 # 내 프로필 수정
GET    /users/me/applications    # 내 지원 내역
```

## 환경 설정

### application.yml 수정

```yaml
spring:
  datasource:
    # 운영 환경에서는 MySQL 사용
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/team_recruit?serverTimezone=UTC
    username: your_username
    password: your_password

jwt:
  secret: your-very-long-secret-key-here
  expiration: 86400000      # 24시간
  refresh-expiration: 604800000  # 7일

sejong:
  portal:
    login-url: https://classic.sejong.ac.kr/userLogin.do
    student-info-url: https://classic.sejong.ac.kr/userSrch.do
```

## 세종대 포털 연동 주의사항

현재 `SejongPortalService`는 세종대학교 학사정보시스템의 **실제 HTML 구조**를 파악하여 수정이 필요합니다.

### 수정 필요 사항:
1. 로그인 폼의 정확한 필드명
2. 학생 정보가 표시되는 페이지의 CSS 셀렉터
3. 인증 쿠키 및 세션 처리 방식

### 개발 중 임시 처리:
현재는 세종대 포털 연동이 실패해도 **테스트 데이터**로 사용자를 생성하도록 구현되어 있습니다.

## 보안 주의사항

1. **JWT Secret Key**: 운영 환경에서는 반드시 안전한 키로 변경
2. **CORS 설정**: 프론트엔드 도메인만 허용하도록 수정
3. **H2 Console**: 운영 환경에서는 비활성화

## 데이터베이스 스키마

### users
- 학번, 이름, 학과, 학년 등 기본 정보
- 기술 스택, 관심 분야 (List)
- GitHub URL, 프로필 이미지

### recruit_posts
- 제목, 설명, 프로젝트 타입
- 필요 기술 스택 (List)
- 모집 인원, 마감일

### applications
- 지원자, 모집 공고
- 지원 동기, 상태 (대기/수락/거절)

## 라이선스

MIT License
