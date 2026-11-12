public class UserCode_eb644a0aa2d843ec8a259af7c4f35eb6 {
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5};
        int even = 0;
        int odd = 0;

        for (int num : numbers) {
            if (num % 2 == 0) {
                even++;
            } else {
                odd++;
            }
        }

        System.out.println("짝수: " + even + ", 홀수: " + odd);
    }
}