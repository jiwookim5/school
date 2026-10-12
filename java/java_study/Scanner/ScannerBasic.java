import java.util.Scanner;

public class ScannerBasic {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("이름을 입력하세요: ");
        String name = sc.nextLine();

        System.out.print("나이를 입력하세요: ");
        int age = sc.nextInt();

        System.out.print("키(cm)를 입력하세요: ");
        double height = sc.nextDouble();

        System.out.printf("%s님은 %d살이고, 키는 %.1fcm 입니다.%n", name, age, height);

        sc.close();
    }
}
