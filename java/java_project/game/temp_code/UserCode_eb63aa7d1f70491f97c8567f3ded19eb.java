import java.util.Scanner;
public class UserCode_eb63aa7d1f70491f97c8567f3ded19eb {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        String reversed = "";
        for (int i = input.length() - 1; i >= 0; i--) {
            reversed += input.charAt(i);
        }
        System.out.println(reversed);
    }
}