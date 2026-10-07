import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class StockTrading {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 입력 받기
        long initialMoney = scanner.nextLong();
        int transactionCount = scanner.nextInt();

        // 거래 데이터 저장
        int[][] transactions = new int[transactionCount][3];
        String[] stocks = new String[transactionCount];
        long currentMoney = initialMoney;

        // 종목별 보유 주식 수
        Map<Character, Integer> stockHolding = new HashMap<>();

        // 거래 처리
        System.out.println("초기투자금 : " + initialMoney + "원");

        for (int i = 0; i < transactionCount; i++) {
            char stock = scanner.next().charAt(0);
            String type = scanner.next();
            long price = scanner.nextLong();
            int quantity = scanner.nextInt();

            // 거래 내역 저장
            stocks[i] = String.valueOf(stock);
            transactions[i][0] = stock - 'A'; // 종목코드
            transactions[i][1] = type.equals("매수") ? 1 : 0; // 매수=1, 매도=0
            transactions[i][2] = (int)price;

            // 거래 출력
            String action = type.equals("매수") ? "매수" : "매도";
            System.out.println("[" + (i+1) + "] (" + action + ") " + stock + " " + price + "원, " + quantity + "주");

            // 자금 및 주식 처리
            if (type.equals("매수")) {
                currentMoney -= price * quantity;
                stockHolding.put(stock, stockHolding.getOrDefault(stock, 0) + quantity);
            } else {
                currentMoney += price * quantity;
                stockHolding.put(stock, stockHolding.getOrDefault(stock, 0) - quantity);
            }
        }

        // 최종 결과 출력
        System.out.println("자금: " + currentMoney);
        System.out.println("종목별 보유 주식수");

        // 알파벳 순서대로 정렬하여 출력
        for (char c = 'A'; c <= 'Z'; c++) {
            if (stockHolding.containsKey(c) && stockHolding.get(c) > 0) {
                System.out.println("  " + c + " " + stockHolding.get(c) + "주");
            }
        }

        scanner.close();
    }
}
