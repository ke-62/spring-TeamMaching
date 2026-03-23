"""
리뷰 전처리 및 욕설/비방 필터링
- 키워드 기반 욕설 감지
- 필터링된 리뷰 제외 후 정상 리뷰만 반환
"""

# 욕설/비방 키워드 목록
BANNED_WORDS = [
    # 직접 욕설
    "씨발", "시발", "ㅅㅂ", "개새", "개새끼", "새끼", "ㅅㄲ",
    "병신", "ㅂㅅ", "미친", "ㅁㅊ", "지랄", "ㅈㄹ", "꺼져",
    "닥쳐", "죽어", "좆", "보지", "자지", "창녀", "년", "놈",
    "쓰레기", "찐따", "빡대가리", "멍청이", "머저리",
    # 비방/인신공격
    "쓸모없", "무능", "최악", "형편없", "최하", "한심",
    "못해", "구려", "별로", "실망", "최저",
]

MIN_REVIEWS = 5


def is_banned(comment: str) -> bool:
    text = comment.lower()
    return any(word in text for word in BANNED_WORDS)


def filter_reviews(reviews: list) -> tuple[list, int]:
    """
    욕설/비방 코멘트 필터링
    Returns:
        clean_reviews: 정상 리뷰 목록
        filtered_count: 필터링된 리뷰 수
    """
    clean = []
    filtered_count = 0

    for r in reviews:
        if is_banned(r.get("comment", "")):
            filtered_count += 1
        else:
            clean.append(r)

    return clean, filtered_count


def validate_reviews(reviews: list) -> tuple[bool, str]:
    """
    리뷰 유효성 검사
    Returns:
        (valid: bool, error_message: str)
    """
    if not reviews:
        return False, "분석할 평가 데이터가 없습니다."

    if len(reviews) < MIN_REVIEWS:
        return False, f"리뷰 데이터가 부족합니다. (현재 {len(reviews)}개 / 최소 {MIN_REVIEWS}개 필요)"

    clean_reviews, filtered_count = filter_reviews(reviews)

    if len(clean_reviews) < MIN_REVIEWS:
        return False, f"유효한 리뷰 데이터가 부족합니다. ({filtered_count}개 필터링됨)"

    return True, ""
