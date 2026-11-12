public class UserCode_f6f4104aa9b940c58963a1db18804d27 {
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