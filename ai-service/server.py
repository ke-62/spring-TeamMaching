"""
Flask 서버 - /analyze-profile 엔드포인트
"""

from flask import Flask, request, jsonify
from model import load_model, generate_hashtags
from filter import validate_reviews, filter_reviews

app = Flask(__name__)


# ── 엔드포인트 ────────────────────────────────────────────────────────────────
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


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})


if __name__ == "__main__":
    load_model()
    app.run(host="0.0.0.0", port=8000, debug=False)
