from flask import Flask, request, jsonify
from sejong_univ_auth import auth, DosejongSession, ClassicSession, MoodlerSession

app = Flask(__name__)


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


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)
