# 전화번호 관리 (Phone Schedule Management) — this 패턴

## 개요
`this` 키워드를 이용하여 클래스 간에 참조를 넘기는 패턴을 학습하는 예제입니다.

## 구성
- **Contact.java** — 연락처 정보(이름, 전화번호) 저장 및 조회
- **Schedule.java** — 일정(요일, 일자, 연락처 목록)을 관리하며 Main의 메소드 호출
- **Main.java** — 연락처 저장 및 Schedule에게 자신(this)을 전달

## 핵심 개념
1. **this 키워드** — 자신의 참조를 다른 클래스에 넘기기
2. **의존성 주입(Dependency Injection)** — Schedule이 Main의 메소드를 사용하기 위해 Main 참조 받기
3. **메소드 위임** — Schedule이 Main의 메소드를 호출하여 기능 사용

## 실행 방법
```bash
javac Contact.java Schedule.java Main.java
java Main
```

## 호출 구조
```
Main
  ├─ Contact 저장
  ├─ Schedule 생성
  └─ Schedule.read(scan, this)  ← Main의 참조를 Schedule에 전달
                   ↓
        Schedule은 Main의 findContact() 메소드 호출
```

## 학습 포인트
- 클래스 간 참조의 필요성
- `this`를 이용한 자신의 참조 전달
- 메소드 호출의 관점에서 책임 분리
