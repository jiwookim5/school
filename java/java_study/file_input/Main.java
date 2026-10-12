import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    ArrayList<Book> bookList = new ArrayList<>();

    void readAllBooks(String filename) {
        Scanner filein = null;

        // (1) 파일 열기
        try {
            filein = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            System.out.printf("파일 없음: %s\n", filename);
            return;
        }

        // (2) hasNext()로 파일 끝까지 읽기
        while (filein.hasNext()) {
            Book book = new Book();
            book.read(filein);
            bookList.add(book);
            book.print();
        }

        // (3) 파일 닫기
        filein.close();
    }

    void run() {
        readAllBooks("books.txt");
        System.out.println("\n총 책 수: " + bookList.size() + "개");
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.run();
    }
}

