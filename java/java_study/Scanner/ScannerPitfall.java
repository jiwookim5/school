import java.util.Scanner;

/*
 * Scanner를 쓸 때 가장 흔한 함정:
 * nextInt(), nextDouble() 같은 메서드는 "숫자"만 읽고
 * 입력 뒤에 남은 개행문자(\n)는 버퍼에 그대로 남겨둔다.
 * 그 상태에서 바로 nextLine()을 호출하면
 * 사용자 입력을 기다리지 않고 빈 문자열("")을 즉시 읽어버린다.
 */
public class ScannerPitfall {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("나이를 입력하세요: ");
        int age = sc.nextInt();

        // 여기서 nextLine()을 바로 호출하면 문제가 생김 (일부러 재현)
        System.out.print("한 줄 메시지를 입력하세요: ");
        String message = sc.nextLine(); // <- age 뒤에 남은 개행만 읽고 끝나버림

        System.out.println("나이: " + age);
        System.out.println("메시지: [" + message + "]"); // 기대와 다르게 빈 문자열이 나옴

        System.out.println("----- 해결 방법: nextInt() 뒤에 sc.nextLine()을 한 번 더 호출해서 버퍼를 비운다 -----");

        System.out.print("다시, 한 줄 메시지를 입력하세요: ");
        String message2 = sc.nextLine(); // 이번엔 정상적으로 사용자 입력을 기다림

        System.out.println("메시지2: [" + message2 + "]");

        sc.close();
    }
}
