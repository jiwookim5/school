import java.util.Scanner;
import java.util.ArrayList;

public class Scanner_array {

    static class Student {
        String name;
        int score;

        Student(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();

        int n = scan.nextInt();
        scan.nextLine();

        for (int i =0 ; i < n; i++) {
            String name = scan.nextLine();

            int score = scan.nextInt();
            scan.nextLine();

            students.add(new Student(name, score));
        }
        System.out.println("결과 : ");
        for (Student s : students) {
            System.out.printf("%s: %d점%n", s.name, s.score);

        }
        scan.close();
    }
}