FROM python:3.10-slim

WORKDIR /app

# 시스템 패키지 설치
RUN apt-get update && apt-get install -y \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# 의존성 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 앱 복사
COPY . .

# HuggingFace Spaces는 포트 7860 사용
EXPOSE 7860

# gunicorn으로 실행
CMD ["gunicorn", "--bind", "0.0.0.0:7860", "--timeout", "600", "--workers", "1", "app:app"]
