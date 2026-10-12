import java.util.Scanner;

public class ScannerBasic {
    public static void main (String[] args) {
        Scanner scan = new Scanner(System.in); // 이게 기본

        String name = scan.nextLine();
        int age = scan.nextInt();

        System.out.printf("%s, %d", name, age);

    }
}

