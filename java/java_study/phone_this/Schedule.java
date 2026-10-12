
import java.util.ArrayList;
import java.util.Scanner;

class Schedule {
    int mon, day;
    ArrayList<Contact> members = new ArrayList<>();

    void read(Scanner scan, Main main) {
        mon = scan.nextInt();
        day = scan.nextInt();

        while (true) {
            String tok = scan.next();
            if (tok.equals("0"))
                break;
            Contact c = main.findContact(tok);
            members.add(c);
        }
    }

    void print() {
        System.out.printf("%d %d. ", mon, day);
        for (Contact c : members) {
            System.out.print(c.name + " ");
        }
        System.out.println();
    }
}