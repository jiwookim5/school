import java.util.Scanner;

class Student {
    int id;
    String name;
    String phone;
    int year;

    void read(Scanner scan) {
        System.out.print("학번: ");
        id = scan.nextInt();
        System.out.print("이름: ");
        name = scan.next();
        System.out.print("전화번호: ");
        phone = scan.next();
        System.out.print("학년: ");
        year = scan.nextInt();
    }

    void print() {
        System.out.printf("%d %s %s (%d학년)\n", id, name, phone, year);
    }

    boolean matches(String kwd) {
        if (kwd.equals(name))
            return true;
        if (kwd.equals("" + id))
            return true;
        if (kwd.equals(phone))
            return true;
        return false;
    }
}
