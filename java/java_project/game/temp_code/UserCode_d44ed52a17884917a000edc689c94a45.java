public class UserCode_d44ed52a17884917a000edc689c94a45 {
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