import java.util.Scanner;

class Student {
    String id;
    String name;
    String phone;
    int year;

    void read(Scanner scan) {
        id = scan.next();
        name = scan.next();
        phone = scan.next();
        year = scan.nextInt();
    }

    void print() {
        System.out.printf("%s %s %s (%d학년)\n", id, name, phone, year);
    }
}
