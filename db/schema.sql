-- 1. 사용자 테이블 (Users)
-- 대학생 및 프로젝트 참여자의 기본 정보와 AI 분석 결과를 담습니다.
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 고유 식별자',
    email VARCHAR(255) UNIQUE NOT NULL COMMENT '계정용 이메일',
    password_hash VARCHAR(255) NOT NULL COMMENT '암호화된 비밀번호',
    full_name VARCHAR(100) NOT NULL COMMENT '본명',
    student_id VARCHAR(20) COMMENT '학번',
    university VARCHAR(100) COMMENT '소속 대학교',
    department VARCHAR(100) COMMENT '소속 학과',
    is_authenticated BOOLEAN DEFAULT FALSE COMMENT '대학생 인증 여부',
    bio TEXT COMMENT '자기소개',
    tech_stack TEXT COMMENT '보유 기술 스택 (JSON 또는 문자열)',
    github_url VARCHAR(255) COMMENT 'GitHub 주소',
    collaboration_keywords TEXT COMMENT 'AI가 추출한 협업 키워드',
    ai_summary TEXT COMMENT 'AI가 분석한 활동 요약문',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '탈퇴 여부 (Soft Delete)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '가입 일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'
);

-- 2. 프로젝트 테이블 (Projects)
-- 팀원 모집 공고 및 프로젝트 상태를 관리합니다.
CREATE TABLE projects (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '프로젝트 고유 번호',
    leader_id INT NOT NULL COMMENT '팀장(생성자) ID',
    title VARCHAR(255) NOT NULL COMMENT '공고 제목',
    content TEXT NOT NULL COMMENT '공고 상세 내용',
    required_roles VARCHAR(255) COMMENT '모집 역할군',
    deadline TIMESTAMP COMMENT '모집 마감일',
    status VARCHAR(50) DEFAULT 'RECRUITING' COMMENT '상태 (RECRUITING, IN_PROGRESS, COMPLETED)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (leader_id) REFERENCES users(id)
);

-- 3. 지원 테이블 (Applications)
-- 특정 프로젝트에 대한 사용자의 참여 신청 내역입니다.
CREATE TABLE applications (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '지원 번호',
    project_id INT NOT NULL COMMENT '대상 프로젝트 ID',
    applicant_id INT NOT NULL COMMENT '지원자 ID',
    message TEXT COMMENT '지원 메시지 및 포부',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT '처리 상태 (PENDING, ACCEPTED, REJECTED)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (applicant_id) REFERENCES users(id)
);

-- 4. 채팅방 테이블 (Chat Rooms)
-- 지원서가 승인(ACCEPTED)되었을 때 자동으로 생성되는 인터뷰/소통방입니다.
CREATE TABLE chat_rooms (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '채팅방 번호',
    application_id INT NOT NULL COMMENT '연결된 지원 건 ID',
    project_id INT NOT NULL COMMENT '연결된 프로젝트 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES applications(id),
    FOREIGN KEY (project_id) REFERENCES projects(id)
);

-- 5. 채팅 메시지 테이블 (Chat Messages)
-- 실시간 대화 내용을 저장합니다.
CREATE TABLE chat_messages (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '메시지 고유 번호',
    room_id INT NOT NULL COMMENT '채팅방 ID',
    sender_id INT NOT NULL COMMENT '발신자 ID',
    content TEXT NOT NULL COMMENT '메시지 내용',
    is_read BOOLEAN DEFAULT FALSE COMMENT '읽음 확인 여부',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

-- 6. 동료 평가 테이블 (Peer Reviews)
-- 프로젝트 종료 후 상호 평가 데이터를 저장하며, AI 분석의 기초 데이터가 됩니다.
CREATE TABLE peer_reviews (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '평가 고유 번호',
    project_id INT NOT NULL COMMENT '프로젝트 ID',
    evaluator_id INT NOT NULL COMMENT '평가 작성자 ID',
    evaluatee_id INT NOT NULL COMMENT '평가 대상자 ID',
    rating_collaboration INT COMMENT '협업 능력 점수 (1~5)',
    rating_technical INT COMMENT '기술 기여도 점수 (1~5)',
    review_text TEXT COMMENT '서술형 평가 내용',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (evaluator_id) REFERENCES users(id),
    FOREIGN KEY (evaluatee_id) REFERENCES users(id)
);