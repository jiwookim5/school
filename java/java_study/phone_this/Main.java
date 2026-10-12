import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    Scanner scan = new Scanner(System.in);
    ArrayList<Contact> contactList = new ArrayList<>();

    Contact findContact(String name) {
        for (Contact c : contactList){
            if (c.name.equals(name))
                return c;
        }
        return null;
    }

    void mymain() {
        for (int i = 0; i < 2; i++) {
            Contact c = new Contact();
            c.read(scan);
            contactList.add(c);
        }
        Schedule s = new Schedule();
        s.read(scan, this);
        s.print();
    }
    public static void main (String[] args) {
        Main m = new Main();
        m.mymain();
    }

}