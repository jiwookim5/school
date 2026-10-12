import java.util.Scanner;

class Book {
    String title;
    String author;
    int year;
    int price;

    void read(Scanner s1) {
        title = s1.next();
        author = s1.next();
        year = s1.nextInt();
        price = s1.nextInt();
    }

    void print() {
        System.out.printf("제목: %s, 저자: %s, 연도: %d, 가격: %d원\n",
                          title, author, year, price);
    }
}