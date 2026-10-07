import java.util.Scanner;

public class Main {
    Scanner scan = new Scanner(System.in);

    void mymain() {
        System.out.print("학생 수: ");
        int n = scan.nextInt();

        Student[] stList = new Student[n];

        for (int i = 0; i < n; i++) {
            stList[i] = new Student();
            stList[i].read(scan);
        }

        for (Student st : stList)
            st.print();
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.mymain();
    }
}
