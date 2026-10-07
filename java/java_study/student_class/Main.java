import java.util.Scanner;

public class Main {
    Scanner scan = new Scanner(System.in);

    void mymain() {
        Student s1 = new Student();
        s1.read(scan);
        s1.print();
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.mymain();
    }
}

