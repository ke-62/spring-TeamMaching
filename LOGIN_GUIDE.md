# 세종대 포털 로그인 연동 가이드

## 🔐 현재 상태: 개발 모드

세종대학교 포털은 **키보드 보안(npPfsCtrl)** 을 사용하여 일반적인 HTTP 크롤링으로는 로그인이 **불가능**합니다.

따라서 현재는 **테스트용 더미 인증 시스템**으로 동작합니다.

---

## 📝 테스트 계정

### 기본 제공 계정

| 학번 | 비밀번호 | 이름 | 학과 | 학년 |
|------|---------|------|------|------|
| 20211234 | test1234 | 이고은 | 컴퓨터공학과 | 3 |
| 20211235 | test1234 | 황채영 | 소프트웨어학과 | 3 |
| 20201111 | test1234 | 김세종 | 인공지능학과 | 4 |
| 20221234 | test1234 | 박대양 | 데이터사이언스학과 | 2 |

### 기타 학번
- 위에 없는 학번으로 로그인 시: **자동으로 더미 계정 생성**
  - 이름: "테스트 사용자"
  - 학과: "컴퓨터공학과"
  - 학년: 3학년
  - 비밀번호 체크 **없음** (아무 비밀번호나 입력 가능)

---

## 🚀 사용 방법

### 1. 백엔드 실행
```bash
cd TeamMaching_backend
mvn spring-boot:run
```

### 2. 프론트엔드에서 로그인 테스트
```
학번: 20211234
비밀번호: test1234
```

### 3. 로그 확인
```
개발 모드: 테스트 계정으로 로그인 시도 - 20211234
테스트 계정 로그인 성공: 이고은 (컴퓨터공학과)
```

---

## ⚙️ 설정 변경

### application.yml
```yaml
sejong:
  portal:
    # 개발 모드 ON/OFF
    dev-mode: true  # true: 테스트 계정, false: 실제 포털 연동 시도
```

---

## 🎯 실제 배포 시 필요한 작업

### 문제점
세종대 포털의 **키보드 보안** 때문에 일반적인 크롤링 불가:
- npPfsCtrl 객체를 통한 암호화 처리
- SSL/HTTPS 필수
- JavaScript 기반 동적 암호화

### 해결 방법

#### 1. 세종대 공식 SSO API 사용 (권장) ⭐
```
세종대학교 IT 지원팀에 문의:
- 이메일: itservice@sejong.ac.kr
- 전화: 02-3408-3488

요청 사항:
- OAuth 2.0 / SAML 기반 SSO 연동
- 학생 정보 조회 API 제공
```

#### 2. 세종대 공식 앱 API 활용
세종대학교 모바일 앱이 사용하는 API를 확인하여 활용
(앱 역공학 필요, 법적 검토 필요)

#### 3. Selenium/Playwright 사용 (비권장)
- 실제 브라우저를 띄워서 자동화
- 키보드 보안 우회 가능하지만:
  - 리소스 많이 사용
  - 불안정함
  - 세종대 보안 정책 위반 가능성

#### 4. 자체 인증 시스템 구축
- 세종대 인증 대신 자체 회원가입 시스템 사용
- 학번 인증은 이메일 인증으로 대체
  - 예: `학번@sju.ac.kr`로 인증 메일 발송

---

## 📋 코드 수정 위치

### 개발 모드 해제 시
```java
// SejongPortalService.java
@Value("${sejong.portal.dev-mode:true}")
private boolean devMode;  // false로 변경하면 실제 연동 시도

public StudentInfo authenticateAndGetStudentInfo(String studentId, String password) {
    if (devMode) {
        // 테스트 계정 사용
    } else {
        // 여기에 실제 SSO API 연동 코드 작성
    }
}
```

### 테스트 계정 추가
```java
// SejongPortalService.java - authenticateTestAccount 메서드
testAccounts.put("20221111", new TestAccount(
    "20221111",
    "password123",
    "홍길동",
    "경영학과",
    2
));
```

---

## 🔍 로그인 플로우

```
1. 사용자가 학번 + 비밀번호 입력
   ↓
2. 프론트엔드 → POST /api/auth/login
   ↓
3. AuthService.login()
   ↓
4. SejongPortalService.authenticateAndGetStudentInfo()
   ↓
5. [개발 모드]
   - TestAccount에서 확인
   - 비밀번호 일치 여부 체크
   - StudentInfo 반환
   ↓
6. DB에 사용자 없으면 자동 생성
   ↓
7. JWT 토큰 발급
   ↓
8. 프론트엔드에 토큰 + 사용자 정보 반환
```

---

## ⚠️ 주의사항

1. **현재는 테스트 전용**입니다
   - 실제 세종대 계정 정보와 무관
   - 비밀번호가 하드코딩되어 있음

2. **실제 배포 시**
   - 반드시 세종대 공식 API 연동 필요
   - 또는 자체 인증 시스템으로 전환

3. **보안**
   - 테스트 계정 비밀번호 노출 주의
   - 실제 배포 시 dev-mode 반드시 false

---

## 💡 추천 로드맵

### Phase 1: 현재 (개발/테스트)
✅ 더미 인증으로 기능 개발 및 테스트

### Phase 2: 중기
- 세종대 IT 지원팀에 SSO API 연동 문의
- 공식 API 사용 가능 여부 확인

### Phase 3: 배포 준비
- **Option A**: 세종대 공식 API 연동 완료
- **Option B**: 자체 인증 시스템으로 전환
  - 회원가입 시 세종대 이메일 인증
  - 학생증 사진 업로드 등으로 학생 인증

---

## 📞 문의

세종대학교 공식 SSO 연동 문의:
- **IT 지원팀**: itservice@sejong.ac.kr / 02-3408-3488
- **개인정보보호**: 1566-0771

키보드 보안 문제:
- **(주)엑센솔루션**: 031-426-6700
