import java.util.ArrayList;
import java.util.Scanner;

public class Department {
    Scanner scan = new Scanner(System.in);
    ArrayList<Student> studentList = new ArrayList<>();

    void mymain() {
        System.out.print("학생 수: ");
        int n = scan.nextInt();

        for (int i = 0; i < n; i++) {
            Student st = new Student();
            st.read(scan);
            studentList.add(st);
        }

        search();
    }

    void search() {
        String kwd = null;
        while (true) {
            System.out.print("검색어: ");
            kwd = scan.next();
            if (kwd.equals("end"))
                break;

            boolean found = false;
            for (Student st : studentList) {
                if (st.matches(kwd)) {
                    st.print();
                    found = true;
                }
            }
            if (!found)
                System.out.println("일치하는 학생이 없습니다.");
        }
    }

    public static void main(String[] args) {
        Department d = new Department();
        d.mymain();
    }
}
