import java.util.Scanner;

public class Scanner_loop {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int sum  =0;
        int  count = 0;

        while (true) {
            System.out.print("숫자 입력 : ");

            if (! scan.hasNextInt()) {
                scan.next();
                continue;
            }
            int num = scan.nextInt();
            if (num < 0) {
                break;
            }
            sum += num;
            count ++;
        }   
        if (count > 0) {
            System.out.printf("%d, %d", sum, count);
        } else {
            System.out.println("fuck");
        }
        scan.close();
    }
}