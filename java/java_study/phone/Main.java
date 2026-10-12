import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    Scanner scan = new Scanner(System.in);
    ArrayList<Contact> contactList = new ArrayList<>();

    // Main이 가진 contactList에서 이름으로 찾아주는 기능
    Contact findContact(String name) {
        for (Contact c : contactList) {
            if (c.name.equals(name)) return c;
        }
        return null;
    }

    void mymain() {
        // 연락처 등록
        for (int i = 0; i < 2; i++) {
            Contact c = new Contact();
            c.read(scan);
            contactList.add(c);
        }

        // 이름으로 연락처 찾기 (Main 자신이 가진 기능이라 this 필요 없음)
        System.out.print("찾을 이름: ");
        String kwd = scan.next();
        Contact found = findContact(kwd);
        if (found != null) found.print();
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.mymain();
    }
}