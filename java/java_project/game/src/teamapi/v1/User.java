package teamapi.v1;
/**
 * 화면에 전달하는 사용자 정보. 비밀번호/해시는 포함하지 않는다.
 * 정답률은 누적 정답 수 / 누적 출제 수 기준이다.
 */
public record User(String id, String name, long totalScore,
                   long correctCount, long totalQuestionCount) implements Manageable {
 public String getId(){return id;}
 public double accuracyPercent(){return totalQuestionCount==0?0:100.0*correctCount/totalQuestionCount;}
}
