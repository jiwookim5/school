import java.util.Scanner;

class Student {
    String name;
    int year;
    int score;

    void read(Scanner scan) {
        name = scan.next();
        year = scan.nextInt();
        score = scan.nextInt();
    }

    void print() {
        System.out.printf("%s %d학년 %d점\n", name, year, score);
    }
}
