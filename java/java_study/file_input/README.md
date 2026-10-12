# 파일 입력 (File Input) — 기본 예제

## 개요
Scanner를 이용하여 파일에서 데이터를 읽는 가장 기본적인 예제입니다.

## 구성
- **Book.java** — 책 정보를 저장하고 입출력하는 클래스
- **Main.java** — 파일을 열고 모든 책 정보를 읽는 메인 클래스
- **books.txt** — 책 정보 입력 데이터

## 핵심 개념
1. **파일 연결 Scanner** — `new Scanner(new File(filename))`
2. **파일 끝 판단** — `filein.hasNext()`로 자동 감지
3. **try-catch** — FileNotFoundException 처리
4. **자원 반납** — `filein.close()`로 파일 닫기

## 실행 방법
```bash
javac Book.java Main.java
java Main
```

## 학습 포인트
- 키보드 Scanner와 파일 Scanner의 차이
- `hasNext()`를 이용한 파일 끝까지 반복 읽기
- Scanner는 배열이 아니므로 인덱스 접근 불가 (while 루프 필수)
