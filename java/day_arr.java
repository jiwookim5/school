
public class day_arr {
public static void main(String[] args){

    int sum = 0, total = 0;
        int[][] arr = {{500, 6000, 2000, 1500}, {2000,500,2000}, {200, 1500, 4000, 8000}};

        int i = 1;
        for (int n[] : arr)
        {
            System.out.printf("%d 일차", i);
            i++;
            for (int s : n){
                System.out.printf("%d", s);
                sum += s;
            }
            System.out.printf("(%d)\n", sum);
            total += sum;            
            sum = 0;
        }
    }
}
 