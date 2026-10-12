# Java 학습 - 객체지향 프로그래밍 (OOP)

Java 기초부터 객체지향 프로그래밍까지의 학습 과정을 정리한 저장소입니다.

## 📁 프로젝트 구조

### 1. Scanner 기초
- **Scanner/** - Scanner 기본 메소드 학습
- **Scanner_ex/** - Scanner 연습 예제

### 2. 클래스와 객체
- **student_class_many/** - 배열을 이용한 여러 학생 관리
- **phone/** - 기본 연락처 관리 프로그램
- **phone_this/** - `this` 키워드를 이용한 클래스 간 참조

### 3. 파일 입력 (File I/O)
- **file_input/** - Scanner를 이용한 파일 읽기 기본
- **file_input_practice/** - ArrayList와 파일 입력 연습

### 4. 상속과 다형성 (Inheritance & Polymorphism)
- **super/** - super() 키워드 학습 (Person → Student)
- **method_override/** - 메소드 오버라이딩 예제 (Animal → Dog, Cat)
- **book_inheritance_practice/** - Book 클래스 상속 실습

## 🎯 핵심 개념

### 파일 입력 (6주차 학습)
```java
Scanner filein = new Scanner(new File("파일명"));
while (filein.hasNext()) {
    // 파일 끝까지 읽기
}
filein.close();
```

**특징:**
- `hasNext()`: 파일 끝을 자동으로 감지
- 종료 신호 없이도 반복 가능
- 키보드 입력과 동일한 사용법

### 상속 (6주차 학습)
```java
public class Student extends Person {
    Student(String name, int age, String major) {
        super(name, age);  // 부모 생성자 호출
        this.major = major;
    }
}
```

**super() 규칙:**
- 반드시 생성자의 첫 줄에 위치
- 부모 필드 초기화 담당
- 부모가 기본 생성자만 있을 경우 자동 호출

### 다형성 (6주차 학습)
```java
Animal dog = new Dog("뽀삐");
Animal cat = new Cat("나비");

dog.speak();  // Dog의 speak() 실행
cat.speak();  // Cat의 speak() 실행
```

**메소드 오버라이딩:**
- 부모 메소드를 자식 클래스에서 재정의
- 메소드명은 같지만 구현은 다름
- @Override 어노테이션으로 컴파일러 검사

## 🚀 실행 방법

### 예제 1: 파일 입력
```bash
cd java/java_study/file_input
javac *.java
java Main
```

### 예제 2: super() 키워드
```bash
cd java/java_study/super
javac *.java
java Main
```

### 예제 3: 메소드 오버라이딩
```bash
cd java/java_study/method_override
javac *.java
java Main
```

## 📚 학습 로드맵

1. ✅ Scanner 기초
2. ✅ 클래스와 객체
3. ✅ 파일 입력 (File I/O)
4. ✅ 상속 (Inheritance)
5. ✅ 다형성 (Polymorphism)
6. ⏳ 메소드 오버라이딩
7. ⏳ 추상 클래스 및 인터페이스

## 💡 주요 개념 정리

| 개념 | 설명 |
|---|---|
| **super()** | 부모 생성자 호출 (부모 필드 초기화) |
| **this** | 자신의 객체 참조 (다른 클래스에 넘김) |
| **extends** | 상속 관계 선언 |
| **@Override** | 메소드 오버라이딩 표시 |
| **업캐스팅** | 자식 객체를 부모 타입으로 취급 |
| **동적 바인딩** | 실행 시 실제 객체 타입의 메소드 호출 |

## 🎓 학습 팁

- 각 예제는 **독립적으로 실행 가능**하도록 구성
- **README.md** 파일로 각 프로젝트의 개념 정리
- **간단한 예제**부터 시작해서 **복잡한 예제**로 진행
- **직접 실행**하면서 이해하기

---

**작성자**: 김지우  
**학습 기간**: 2026년 10월  
**목표**: Java 객체지향 프로그래밍 기초 마스터
