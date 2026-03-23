"""
Qwen2.5-3B LoRA 파인튜닝 스크립트
- 개인 PC GPU (6~8GB VRAM) 기준
- QLoRA (4bit 양자화) 적용으로 메모리 절약
"""

import json
import torch
from datasets import Dataset
from transformers import (
    AutoTokenizer,
    AutoModelForCausalLM,
    BitsAndBytesConfig,
    TrainingArguments,
)
from peft import LoraConfig, get_peft_model
from trl import SFTTrainer

# ── 설정 ──────────────────────────────────────────────────────────────────────
MODEL_NAME = "Qwen/Qwen2.5-3B-Instruct"
OUTPUT_DIR = "./model/finetuned"
DATA_PATH = "./data/train.json"

# 4bit 양자화 설정 (VRAM 절약)
bnb_config = BitsAndBytesConfig(
    load_in_4bit=True,
    bnb_4bit_use_double_quant=True,
    bnb_4bit_quant_type="nf4",
    bnb_4bit_compute_dtype=torch.bfloat16,
)

# LoRA 설정 (가벼운 파인튜닝)
lora_config = LoraConfig(
    r=8,                    # rank (낮을수록 가벼움)
    lora_alpha=16,
    target_modules=["q_proj", "v_proj", "k_proj", "o_proj"],
    lora_dropout=0.05,
    bias="none",
    task_type="CAUSAL_LM",
)

# ── 데이터 전처리 ─────────────────────────────────────────────────────────────
def format_prompt(example: dict) -> str:
    """입력 데이터를 모델 학습용 프롬프트로 변환"""
    data = example["input"]
    reviews_text = "\n".join([
        f"- [{r['projectTitle']}] 협업:{r['collaborationScore']} 기술:{r['technicalScore']} | {r['comment']}"
        for r in data["reviews"]
    ])

    prompt = f"""<|im_start|>system
당신은 팀 매칭 플랫폼의 AI 분석가입니다. 개발자의 프로필과 리뷰 데이터를 바탕으로 2~3문장의 핵심 요약을 작성합니다.<|im_end|>
<|im_start|>user
다음 개발자의 프로필을 분석하고 요약해주세요.

이름: {data['userName']}
학과: {data['department']}
기술스택: {', '.join(data['techStacks'])}
총 프로젝트: {data['totalProjects']}개
평균 협업 점수: {data['averageCollaboration']}
평균 기술 점수: {data['averageTechnical']}

리뷰:
{reviews_text}<|im_end|>
<|im_start|>assistant
{example['output']}<|im_end|>"""
    return prompt


def load_dataset(path: str) -> Dataset:
    with open(path, "r", encoding="utf-8") as f:
        raw = json.load(f)
    texts = [{"text": format_prompt(item)} for item in raw]
    return Dataset.from_list(texts)


# ── 학습 ──────────────────────────────────────────────────────────────────────
def train():
    print("모델 로딩 중...")
    tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME, trust_remote_code=True)
    tokenizer.pad_token = tokenizer.eos_token

    model = AutoModelForCausalLM.from_pretrained(
        MODEL_NAME,
        quantization_config=bnb_config,
        device_map="auto",
        trust_remote_code=True,
    )
    model = get_peft_model(model, lora_config)
    model.print_trainable_parameters()

    dataset = load_dataset(DATA_PATH)
    print(f"학습 데이터: {len(dataset)}개")

    training_args = TrainingArguments(
        output_dir=OUTPUT_DIR,
        num_train_epochs=3,
        per_device_train_batch_size=1,
        gradient_accumulation_steps=4,
        learning_rate=2e-4,
        fp16=True,
        logging_steps=10,
        save_strategy="epoch",
        warmup_ratio=0.05,
        lr_scheduler_type="cosine",
        report_to="none",
    )

    trainer = SFTTrainer(
        model=model,
        tokenizer=tokenizer,
        train_dataset=dataset,
        dataset_text_field="text",
        max_seq_length=1024,
        args=training_args,
    )

    print("학습 시작...")
    trainer.train()
    trainer.save_model(OUTPUT_DIR)
    tokenizer.save_pretrained(OUTPUT_DIR)
    print(f"모델 저장 완료: {OUTPUT_DIR}")


if __name__ == "__main__":
    train()
