"""
Flask 서버 - AI 프로필 분석 + 세종대 인증 통합 서비스
"""

from flask import Flask, request, jsonify
from model import load_model, generate_hashtags
from filter import validate_reviews, filter_reviews
from sejong_univ_auth import auth, DosejongSession

app = Flask(__name__)


# ── AI 프로필 분석 ─────────────────────────────────────────────────────────────
@app.route("/analyze-profile", methods=["POST"])
def analyze_profile():
    data = request.get_json()

    if not data:
        return jsonify({"success": False, "error": "요청 데이터가 없습니다."}), 400

    reviews = data.get("reviews", [])

    # 리뷰 유효성 검사 (5개 미만, 욕설 필터링 후 부족 등)
    valid, error_msg = validate_reviews(reviews)
    if not valid:
        return jsonify({"success": False, "error": error_msg}), 400

    # 욕설 필터링
    clean_reviews, filtered_count = filter_reviews(reviews)

    try:
        hashtags = generate_hashtags(data, clean_reviews)

        return jsonify({
            "success": True,
            "hashtags": hashtags,
            "filteredCount": filtered_count
        })

    except Exception as e:
        return jsonify({"success": False, "error": f"분석 중 오류 발생: {str(e)}"}), 500


# ── 세종대 인증 ────────────────────────────────────────────────────────────────
@app.route("/auth", methods=["POST"])
def authenticate():
    data = request.get_json()
    if not data or "id" not in data or "password" not in data:
        return jsonify({"success": False, "message": "id와 password를 입력해주세요."}), 400

    student_id = data["id"]
    password = data["password"]

    try:
        result = auth(id=student_id, password=password, methods=DosejongSession)

        if result.is_auth:
            return jsonify({
                "success": True,
                "name": result.body.get("name", ""),
                "major": result.body.get("major", ""),
            })
        else:
            return jsonify({"success": False, "message": "인증 실패: 학번 또는 비밀번호를 확인해주세요."})
    except Exception as e:
        return jsonify({"success": False, "message": f"인증 서버 오류: {str(e)}"}), 500


# ── 헬스체크 ───────────────────────────────────────────────────────────────────
@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})


if __name__ == "__main__":
    load_model()
    app.run(host="0.0.0.0", port=8000, debug=False)
