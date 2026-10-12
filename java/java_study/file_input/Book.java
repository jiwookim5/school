import java.util.Scanner;

class Book {
    String title;
    String author;
    int year;
    int price;

    void read(Scanner scan) {
        title = scan.next();
        author = scan.next();
        year = scan.nextInt();
        price = scan.nextInt();
    }

    void print() {
        System.out.printf("제목: %s, 저자: %s, 연도: %d, 가격: %d원\n",
                          title, author, year, price);
    }
}
