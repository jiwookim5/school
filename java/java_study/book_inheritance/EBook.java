import java.util.Scanner;

public class EBook extends Book {
    String url;
    String format;

    @Override
    void read(Scanner scan) {
        super.read(scan);       // 부모(Book)가 읽는 필드는 부모 메소드 재사용
        url = scan.next();      // 추가로 읽어야 할 필드만 직접 입력
        format = scan.next();
    }

    @Override
    void printBookType() {
        System.out.print("[전자책] ");
    }

    @Override
    public void print() {
        super.print();          // 부모의 출력(공통 부분)을 그대로 재사용
        System.out.printf("\t%s [%s]\n", url, format);
    }

    @Override
    boolean matches(String kwd) {
        if (super.matches(kwd))   // 부모 것은 부모 메소드로 재사용 (상속의 SRP)
            return true;
        if ("전자책".equals(kwd))
            return true;
        if (url.contains(kwd))
            return true;
        if (format.equals(kwd))
            return true;
        return false;
    }
}
