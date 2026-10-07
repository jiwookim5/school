import java.util.Scanner;

class Student {
    int id;
    String name;
    String phone;
    int year;
    int score;

    void read(Scanner scan) {
        System.out.print("학번: ");
        id = scan.nextInt();
        System.out.print("이름: ");
        name = scan.next();
        System.out.print("전화번호: ");
        phone = scan.next();
        System.out.print("학년: ");
        year = scan.nextInt();
    }

    void print() {
        System.out.printf("%d %s %s (%d학년), %d점\n", id, name, phone, year, score);
    }

    void inputScore(Scanner scan) {
        System.out.printf("%s: ", name);
        score = scan.nextInt();
    }

    // 단일 키워드 부분 일치 검색 
    boolean matches(String kwd) {
        if (kwd.length() == 1 && Character.isDigit(kwd.charAt(0))) {
            // 숫자 한 자리면 학년에만 매치 (학번/전화번호에는 매치 안 함)
            return year == Integer.parseInt(kwd);
        }
        if (("" + id).contains(kwd))
            return true;
        if (name.contains(kwd))
            return true;
        if (phone.contains(kwd))
            return true;
        return false;
    }

    // 멀티 키워드 검색 - 모두 매치되어야 true, "-"로 시작하면 제외 키워드 (29~31페이지)
    boolean matches(String[] kwds) {
        for (String kwd : kwds) {
            if (kwd.charAt(0) == '-') {
                String realKwd = kwd.substring(1);
                if (matches(realKwd))
                    return false;   // 제외 키워드가 포함되면 실패
            } else {
                if (!matches(kwd))
                    return false;   // 포함되어야 할 키워드가 없으면 실패
            }
        }
        return true;
    }

    // 점수 구간 검색: start 이상 end 미만 
    boolean matchesScore(int start, int end) {
        return score >= start && score < end;
    }
}
