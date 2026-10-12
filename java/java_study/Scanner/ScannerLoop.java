import java.util.Scanner;

/*
 * hasNextInt()를 이용해 "입력이 끝날 때까지" 반복해서 읽는 패턴.
 * 음수를 입력하면 종료되도록 구성했다.
 */
public class ScannerLoop {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int sum = 0;
        int count = 0;

        System.out.println("양의 정수를 입력하세요. 음수를 입력하면 종료됩니다.");

        while (true) {
            System.out.print("숫자 입력: ");

            if (!sc.hasNextInt()) {
                System.out.println("정수가 아닙니다. 다시 입력하세요.");
                sc.next(); // 잘못된 입력 버리기
                continue;
            }

            int num = sc.nextInt();

            if (num < 0) {
                System.out.println("종료합니다.");
                break;
            }

            sum += num;
            count++;
        }

        if (count > 0) {
            System.out.printf("입력 개수: %d, 합계: %d, 평균: %.2f%n", count, sum, (double) sum / count);
        } else {
            System.out.println("입력된 숫자가 없습니다.");
        }

        sc.close();
    }
}
