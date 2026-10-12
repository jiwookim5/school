import java.util.ArrayList;
import java.util.Scanner;

public class Book {
    String title;
    String pub;
    String isbn;
    int year;
    ArrayList<String> authors = new ArrayList<>();
    int price;

    void read(Scanner scan) {
        title = scan.next();
        pub = scan.next();
        isbn = scan.next();
        year = scan.nextInt();

        authors = new ArrayList<>();
        String token;
        while (true) {
            token = scan.next();
            if (token.equals("0"))
                break;
            authors.add(token);
        }
        price = scan.nextInt();
    }

    // 가상함수 - 서브클래스가 오버라이드해서 자기 종류를 알려줌
    void printBookType() {
        System.out.print("[일반책] ");
    }

    public void print() {
        printBookType();   // 동적바인딩: this의 실제 타입에 맞는 printBookType 호출됨
        System.out.printf("%s (%s/%s/%d년) [%d원] 저자:", title, pub, isbn, year, price);
        for (String a : authors) {
            System.out.printf(" %s", a);
        }
        System.out.println();
    }

    boolean matches(String kwd) {
        if ("일반책".equals(kwd))
            return true;
        if (title.contains(kwd))
            return true;
        if (pub.contains(kwd))
            return true;
        if (isbn.equals(kwd))
            return true;
        for (String a : authors) {
            if (a.contains(kwd))
                return true;
        }
        return false;
    }
}
