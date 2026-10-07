# student_class_many

`student_class`를 확장해서 학생 여러 명을 배열로 관리하는 예제.

## 다루는 내용
- 입력받은 학생 수(`n`)만큼 `Student` 배열 생성 (`new Student[n]`)
- 배열 생성과 각 요소(객체) 생성은 별개 — 배열 생성 후 반복문으로 각 칸마다 `new Student()` 필요
- 객체를 새로 만드는 입력 반복문은 일반 `for`, 값을 읽기만 하는 출력 반복문은 `for-each` 사용

## 파일
- `Student.java` — 학생 클래스 (student_class와 동일)
- `Main.java` — 학생 수를 입력받아 배열로 관리, 전체 입력 후 전체 출력

## 실행
```
javac Student.java Main.java
java Main
```

## 입력 예시
```
3
진달래 2 80
조용한 2 75
이여진 1 57
```

## 출력 예시
```
진달래 2학년 80점
조용한 2학년 75점
이여진 1학년 57점
```
