# 파일 입력 실습 (File Input Practice)

## 개요
Scanner를 이용하여 파일에서 학생 정보를 읽고 ArrayList에 저장하는 예제입니다.

## 구성
- **Student.java** — 학생 정보(학번, 이름, 전화번호, 학년)를 저장하는 클래스
- **Main.java** — 파일에서 모든 학생을 읽고 출력하는 메인 클래스
- **students.txt** — 학생 정보 입력 데이터

## 핵심 개념
1. **ArrayList에 파일 데이터 저장** — 반복하면서 계속 새로운 객체 생성
2. **openFile() 메소드** — 파일 열기 및 예외 처리를 한 곳에서 담당
3. **readAll() 메소드** — `hasNext()` 루프로 파일 끝까지 읽기
4. **입력 즉시 출력** — 디버깅을 위한 입력 결과 검증

## 실행 방법
```bash
javac Student.java Main.java
java Main
```

## 데이터 형식 (students.txt)
```
학번 이름 전화번호 학년
학번 이름 전화번호 학년
...
```

## 학습 포인트
- 파일 입력 방식의 세 단계: 열기 → 읽기 → 닫기
- 키보드 입력과 달리 종료 신호("0" 등)가 필요 없음
- `while (filein.hasNext())`로 자동으로 파일 끝을 감지
