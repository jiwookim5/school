# 책(Book) 상속 — 6주차 과제

## 개요
Book 클래스를 부모 클래스로 하여 EBook(전자책)과 AppendixBook(부록책)을 상속받는 예제입니다. 상속, 메소드 오버라이딩, 다형성을 학습합니다.

## 구성
- **Book.java** — 기본 책 정보(제목, 저자, ISBN, 연도, 가격) 및 메소드
- **EBook.java** — Book을 상속받은 전자책 클래스 (파일 크기 추가)
- **AppendixBook.java** — Book을 상속받은 부록책 클래스 (부록 수 추가)
- **BookStore.java** — 책 정보를 파일에서 읽고 검색 기능 제공
- **book.txt** — 책 정보 입력 데이터

## 핵심 개념
1. **상속(Inheritance)** — `extends` 키워드로 부모 클래스 기능 물려받기
2. **메소드 오버라이딩(Overriding)** — 자식 클래스에서 부모 메소드를 재정의
3. **super() 호출** — 생성자에서 부모 생성자를 먼저 실행
4. **다형성(Polymorphism)** — 부모 타입 변수로 자식 객체 참조

## 실행 방법
```bash
javac Book.java EBook.java AppendixBook.java BookStore.java
java BookStore
```

## 기능
- 파일에서 모든 책 정보 읽기
- 책 정보 전체 출력
- 검색 기능 (제목/저자로 검색)

## 학습 포인트
- Book, EBook, AppendixBook의 상속 관계
- 각 클래스의 `print()` 메소드 오버라이딩
- 부모 클래스 메소드 호출 시 `super.print()` 사용법
