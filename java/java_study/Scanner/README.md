# Scanner 기초 (Basic Scanner Usage)

## 개요
Scanner 클래스의 기본적인 사용법을 학습하는 예제 모음입니다.

## 구성
- **ScannerBasic.java** — Scanner 기본 메소드 (next, nextInt, nextDouble 등)
- **ScannerLoop.java** — Scanner를 이용한 반복 입력
- **ScannerMixedInput.java** — 문자와 숫자 혼합 입력 처리
- **ScannerPitfall.java** — Scanner 사용 시 주의할 점 및 에러

## 핵심 개념
1. **`next()`** — 공백을 기준으로 한 단어 읽기
2. **`nextInt()`, `nextDouble()`** — 정수, 실수 읽기
3. **`nextLine()`** — 개행까지의 전체 줄 읽기
4. **버퍼 문제** — `nextInt()` 후 `nextLine()` 사용 시 개행 문자 처리

## 학습 포인트
- `next()`와 `nextLine()`의 차이
- `nextInt()` 후 남는 개행 문자 처리 (`scan.nextLine()` 추가)
- 입력 데이터 형식에 맞춰 적절한 메소드 선택
