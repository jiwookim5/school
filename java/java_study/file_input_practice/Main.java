import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    ArrayList<Student> studentList = new ArrayList<>();

    // (1) 파일 열기 실패 처리 - 11페이지
    Scanner openFile(String filename) {
        Scanner filein = null;
        try {
            filein = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            System.out.printf("파일 오픈 실패: %s\n", filename);
            throw new RuntimeException(e);
        }
        return filein;
    }

    // (3) readAll 메소드 - 12페이지
    void readAll(String filename) {
        Scanner filein = openFile(filename);1

        // (4) hasNext()로 파일 끝까지 읽기 - 13페이지
        Student st = null;
        while (filein.hasNext()) {
            st = new Student();
            st.read(filein);
            studentList.add(st);
            st.print();   // 입력 결과 바로 확인 - 14페이지
        }

        filein.close();   // 입력 끝나면 close
    }

    void run() {
        readAll("students.txt");
        System.out.println("---- 전체 학생 수: " + studentList.size() + " ----");
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.run();
    }
}
