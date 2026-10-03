import java.util.ArrayList;
import java.util.Scanner;
public class Department {
    Scanner scan = new Scanner(System.in);
    ArrayList<Student> stList = new ArrayList<>();
    void mymain() 
    {
        readAllStudents();
        run_menu();
    }
    void run_menu() {
        while (true) {
            IO.print("(1) 전체 출력 (2) 점수입력 (3) 검색,  (0) 종료:");
            int menu = scan.nextInt();
            switch (menu) {
            case 1 -> printAll();
            case 2 -> inputScores();
            case 3 -> search();
            }
        }
    }
    void inputScores() {
        for (Student st: stList)
            90
            
    }
    void search() {
        String kwd;
        while (true) {
        }
    }
    void readAllStudents() {
        Student st = null;
        while (true) {
            int id = scan.nextInt();
            if (id == 0) break;
            st = new Student();
            st.read(scan, id);
            stList.add(st);
        }
    }
    void printAll() {
        for (Student st: stList)
            st.print();
    }
    public static void main(String[] args) throws Exception {
        Department a = new Department();
        a.mymain();
    }
}
