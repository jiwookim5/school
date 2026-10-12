import java.util.ArrayList;
import java.util.Scanner;

// 부록책: 별책(부록)을 여러 권 가지는 책
public class AppendixBook extends Book {
    ArrayList<String> extras = new ArrayList<>();

    @Override
    void read(Scanner scan) {
        super.read(scan);
        int n = scan.nextInt();          // 별책 권수
        extras = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            extras.add(scan.next());     // 별책 제목을 n개만큼 읽기
        }
    }

    @Override
    void printBookType() {
        System.out.print("[부록책] ");
    }

    @Override
    public void print() {
        super.print();
        System.out.printf("\t별책 %d권:", extras.size());
        for (String e : extras) {
            System.out.printf(" %s", e);
        }
        System.out.println();
    }

    @Override
    boolean matches(String kwd) {
        if (super.matches(kwd))
            return true;
        if ("부록책".equals(kwd))
            return true;
        for (String e : extras) {
            if (e.contains(kwd))
                return true;
        }
        return false;
    }
}
