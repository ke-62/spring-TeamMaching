"""
모델 로드 및 추론
- Qwen2.5-1.5B-Instruct (CPU 모드)
- LoRA 파인튜닝 모델 자동 감지
"""

import os
import re
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM
from peft import PeftModel

BASE_MODEL = "Qwen/Qwen2.5-1.5B-Instruct"
FINETUNED_DIR = "./model/finetuned"

tokenizer = None
model = None


def load_model():
    global tokenizer, model

    use_finetuned = os.path.exists(FINETUNED_DIR)
    print("모델 로딩 중... (CPU 모드, 1~2분 소요)")

    tokenizer = AutoTokenizer.from_pretrained(
        FINETUNED_DIR if use_finetuned else BASE_MODEL,
        trust_remote_code=True,
    )
    tokenizer.pad_token = tokenizer.eos_token

    base = AutoModelForCausalLM.from_pretrained(
        BASE_MODEL,
        torch_dtype=torch.float32,
        device_map="cpu",
        trust_remote_code=True,
    )

    if use_finetuned:
        model = PeftModel.from_pretrained(base, FINETUNED_DIR)
        print("파인튜닝 모델 로드 완료")
    else:
        model = base
        print("베이스 모델 로드 완료 (파인튜닝 모델 없음)")


def build_prompt(data: dict, reviews: list) -> str:
    comments_text = "\n".join([f"- {r['comment']}" for r in reviews])

    return f"""<|im_start|>system
당신은 팀 매칭 플랫폼의 AI 분석가입니다.
아래 규칙을 반드시 따르세요.

[규칙]
1. 해시태그는 4~5개 작성하세요.
2. 기술 스택은 그대로 해시태그로 포함하세요. (예: #React, #SpringBoot)
3. 협업 능력, 기술력, 소통 능력은 이미 점수로 표시되므로 해시태그에 절대 포함하지 마세요.
   - 금지 예시: #소통잘함, #기술력좋음, #협업능력우수, #커뮤니케이션 등
4. 나머지 해시태그는 리뷰 코멘트를 기반으로 이 개발자가 어떤 사람인지를 나타내는 표현으로 작성하세요.
   - 단순 단어가 아닌 이 사람을 설명하는 문구로 작성하세요.
   - 좋은 예시: #문서화에진심인개발자, #꼼꼼한코드리뷰어, #같이일하고싶은팀원
5. 반드시 아래 형식으로만 출력하세요: #태그1, #태그2, #태그3, #태그4<|im_end|>
<|im_start|>user
개발자 정보:
기술스택: {', '.join(data['techStacks'])}

리뷰 코멘트:
{comments_text}<|im_end|>
<|im_start|>assistant
"""


def generate_hashtags(data: dict, reviews: list) -> list[str]:
    prompt = build_prompt(data, reviews)
    inputs = tokenizer(prompt, return_tensors="pt").to(model.device)

    with torch.no_grad():
        outputs = model.generate(
            **inputs,
            max_new_tokens=80,
            temperature=0.7,
            do_sample=True,
            top_p=0.9,
            repetition_penalty=1.1,
            eos_token_id=tokenizer.eos_token_id,
            pad_token_id=tokenizer.eos_token_id,
        )

    generated = outputs[0][inputs["input_ids"].shape[1]:]
    raw_text = tokenizer.decode(generated, skip_special_tokens=True).strip()
    raw_text = raw_text.replace("<|im_end|>", "").strip()

    tags = re.findall(r"#\S+", raw_text)
    return [t.rstrip(",").strip() for t in tags][:5]
