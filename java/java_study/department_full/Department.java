import java.util.ArrayList;
import java.util.Scanner;

public class Department {
    Scanner scan = new Scanner(System.in);
    ArrayList<Student> studentList = new ArrayList<>();

    void readAllStudents() {
        System.out.print("학생수: ");
        int num = scan.nextInt();
        for (int i = 0; i < num; i++) {
            Student st = new Student();
            st.read(scan);
            studentList.add(st);
        }
    }

    void printAllStudents() {
        for (Student st : studentList)
            st.print();
    }

    void inputScores() {
        System.out.println("학생 점수 입력");
        for (Student st : studentList)
            st.inputScore(scan);
    }

    void run() {
        int menu;
        while (true) {
            System.out.print("(1) 전체 출력 (2) 점수입력 (3) 검색 (0) 종료: ");
            menu = scan.nextInt();
            switch (menu) {
                case 1: printAllStudents(); break;
                case 2: inputScores(); break;
                case 3: searchMenu(); break;
                case 0: return;
                default: break;
            }
        }
    }

    void searchMenu() {
        int menu;
        while (true) {
            System.out.print("(1) 학번/이름 검색 (2) 통합검색 (3) 점수 검색 (0) 종료: ");
            menu = scan.nextInt();
            switch (menu) {
                case 1: search(); break;
                case 2: multiSearch(); break;
                case 3: searchScores(); break;
                case 0: return;
                default: break;
            }
        }
    }

    // 1단계 ~ 3단계: 이름/학번/전화번호 단일 키워드 부분 검색 
    void search() {
        System.out.print("검색키워드: ");
        String kwd = scan.next();
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

    // 4단계: 멀티키워드 + 제외키워드 검색 
    void multiSearch() {
        scan.nextLine();  // 이전 nextInt()가 남긴 개행 문자 제거
        System.out.print("검색키워드 여러개(빈칸으로 구분): ");
        String line = scan.nextLine();
        String[] kwdArr = line.split(" ");
        for (Student st : studentList)
            if (st.matches(kwdArr))
                st.print();
    }

    // 점수 구간 검색 
    void searchScores() {
        System.out.print("검색할 점수 구간: ");
        String temp = scan.next();
        int start = temp.equals("-") ? 0 : Integer.parseInt(temp);

        temp = scan.next();
        int end = temp.equals("-") ? 101 : Integer.parseInt(temp);

        for (Student st : studentList)
            if (st.matchesScore(start, end))
                st.print();
    }

    public static void main(String[] args) {
        Department d = new Department();
        d.readAllStudents();
        d.run();
    }
}
