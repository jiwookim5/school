import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class BookStore {
    Scanner scan = new Scanner(System.in);
    ArrayList<Book> bookList = new ArrayList<>();

    void run() {
        readAllBooks("book.txt");
        printAllBooks();
        search();
    }

    void printAllBooks() {
        for (Book b : bookList)
            b.print();   // b는 Book 타입이지만, 가상함수 호출로 실제 객체(Book/EBook/AppendixBook)에 맞는 print() 실행
    }

    void readAllBooks(String filename) {
        Scanner filein = openFile(filename);
        Book b = null;
        while (filein.hasNext()) {
            int n = filein.nextInt();   // 1: 일반책, 2: 전자책, 3: 부록책
            switch (n) {
                case 1: b = new Book(); break;
                case 2: b = new EBook(); break;
                case 3: b = new AppendixBook(); break;
                default: break;
            }
            b.read(filein);
            bookList.add(b);
        }
        filein.close();
    }

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

    void search() {
        String kwd = null;
        while (true) {
            System.out.print(">> 검색할 키워드(end 입력시 종료): ");
            kwd = scan.next();
            if (kwd.equals("end"))
                break;

            boolean found = false;
            for (Book b : bookList) {
                if (b.matches(kwd)) {
                    b.print();
                    found = true;
                }
            }
            if (!found)
                System.out.println("검색 결과가 없습니다.");
        }
    }

    public static void main(String[] args) {
        BookStore store = new BookStore();
        store.run();
    }
}
