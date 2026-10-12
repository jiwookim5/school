import java.util.ArrayList;
import java.util.Scanner;

class Contact {
    String name;
    String phone;

    void read(Scanner scan) {
        name = scan.next();
        phone = scan.next();
    }

    void print() {
        System.out.printf("%s %s\n", name, phone);
    }
}