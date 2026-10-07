# department_search

학생 여러 명 중에서 키워드로 원하는 학생을 검색하는 예제. SRP(단일 책임 원칙) 학습용.

## 다루는 내용
- `Department`가 학생의 필드를 직접 비교하지 않고, `Student`에게 `matches()`로 "너 이 키워드에 맞니?"라고 물어보는 방식
- `Student.matches(String kwd)`: 이름, 학번(int → String 변환), 전화번호 중 하나라도 정확히 일치하면 true (통합검색)
- `ArrayList<Student>`로 학생 목록 관리

## 파일
- `Student.java` — id, name, phone, year 필드 + read/print/matches 메소드
- `Department.java` — 학생 등록 후 반복 검색 (키워드 "end" 입력 시 종료)

## 실행
```
javac Student.java Department.java
java Department
```

## 입력 예시
```
3
201710140
김원철
010-7757-1256
3
201411012
이가영
010-7722-1256
3
201711018
이종철
010-7788-1256
3
김원철
end
```

## 출력 예시
```
201710140 김원철 010-7757-1256 (3학년)
```

## 참고
- `equals()` 기반 완전 일치 검색만 지원 (부분 일치는 `department_full`에서 확장)
