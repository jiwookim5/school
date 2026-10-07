# student_class

학생 한 명의 정보(이름, 학년, 점수)를 입력받아 출력하는 기본 예제.

## 다루는 내용
- `Student` 클래스에 필드(`name`, `year`, `score`)와 메소드(`read`, `print`) 정의
- `main`(static)은 객체를 생성하고 그 객체의 메소드를 호출하는 역할만 함
- "객체의 일은 객체에게" 원칙: `Main`은 `Student`의 필드에 직접 접근하지 않고, `read()`/`print()` 메소드를 통해서만 접근
- `this` 키워드와 생략 규칙

## 파일
- `Student.java` — 학생 클래스 (필드 + read/print 메소드)
- `Main.java` — 학생 객체 1명을 생성해서 입력받고 출력

## 실행
```
javac Student.java Main.java
java Main
```

## 입력 예시
```
진달래 2 80
```

## 출력 예시
```
진달래 2학년 80점
```
