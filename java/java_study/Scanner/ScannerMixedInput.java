import java.util.ArrayList;
import java.util.Scanner;

/*
 * 여러 명의 학생 정보(이름, 점수)를 입력받아 리스트에 저장하는 실습.
 * nextLine()과 nextInt()를 섞어 쓸 때 버퍼 처리에 주의한다.
 */
public class ScannerMixedInput {

    static class Student {
        String name;
        int score;

        Student(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();

        System.out.print("입력할 학생 수: ");
        int n = sc.nextInt();
        sc.nextLine(); // nextInt() 뒤에 남은 개행 비우기

        for (int i = 0; i < n; i++) {
            System.out.printf("[%d번째 학생] 이름: ", i + 1);
            String name = sc.nextLine();

            System.out.printf("[%d번째 학생] 점수: ", i + 1);
            int score = sc.nextInt();
            sc.nextLine(); // 다음 루프에서 nextLine()을 또 쓰므로 여기서도 비워준다

            students.add(new Student(name, score));
        }

        System.out.println("----- 입력 결과 -----");
        for (Student s : students) {
            System.out.printf("%s: %d점%n", s.name, s.score);
        }

        sc.close();
    }
}
