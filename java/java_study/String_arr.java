// 헤더 느낌
import java.util.Scanner;

public class String_arr {  //클래스 안에
    public static void main(String[] args){  // main 메소드
        Scanner s = new Scanner(System.in);  //input

        int n = s.nextInt();  // 맨 처음 정수가 n으로 들어감

        String[] words = new String[n];
        for (int i = 0; i < n; i++){
            words[i] = s.next(); // 배열 input으로 초기화
        }

        String longest = words[0];
        for (String word : words) {
            if (word.length() >= 5) {
                System.out.println(word + " 길이:" + word.length());
            }
            if (word.length() > longest.length()) {
                longest = word;
            }
        }
          System.out.println("제일 긴 스트링:" + longest);
    }
}




public class MyClass {

    void Mymain();{
        System.out.printf("hellow\n");
    }

    public static void main (String[] args) {
        MyClass my = new MyClass;
        my.Mymain;
    }  
}