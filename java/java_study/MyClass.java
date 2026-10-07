import java.util.ArrayList;
import java.util.Scanner;

public class MyClass {
    public static void main(String[] args) {
        var stList = new ArrayList<Student>();

        // 1. add() — 리스트에 객체 추가
        Student s1 = new Student("진달래", 2, 80);
        Student s2 = new Student("조용한", 2, 75);
        Student s3 = new Student("이여진", 1, 57);

        stList.add(s1);
        stList.add(s2);
        stList.add(s3);

        System.out.println("현재 리스트 크기: " + stList.size());  // 3

        // 2. get() — index번째 객체 가져오기
        Student st = stList.get(1);        // index 1번 = 두 번째 저장된 객체
        System.out.println("get(1): " + st.name);   // 조용한

        // 3. indexOf() — 특정 객체가 몇 번째에 있는지 찾기
        int index = stList.indexOf(s3);
        System.out.println("s3의 인덱스: " + index); // 2

        int notFoundIndex = stList.indexOf(new Student("없는사람", 0, 0));
        System.out.println("없는 객체의 인덱스: " + notFoundIndex); // -1

        // 4. remove() — index번째 객체를 삭제하고, 삭제된 객체를 반환
        Student removed = stList.remove(0);  // 0번째(진달래) 삭제
        System.out.println("삭제된 학생: " + removed.name);  // 진달래
        System.out.println("삭제 후 리스트 크기: " + stList.size());  // 2

         // 남은 학생들 출력
        for (Student remain : stList) {
            System.out.println("남은 학생: " + remain.name);
        }
    }
}

class Student {
    String name;
    int year;
    int score;

    Student(String name, int year, int score) {
        this.name = name;
        this.year = year;
        this.score = score;
    }
}