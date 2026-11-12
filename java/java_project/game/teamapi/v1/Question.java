package teamapi.v1;
import java.util.List;
/**
 * 프론트에 전달할 문제. 백엔드 내부 정답/해설 저장 엔티티와 구분한다.
 * code는 코드가 없으면 빈 문자열, choices는 주관식이면 빈 목록.
 */
public record Question(String questionId, QuestionType type, String prompt,
                       String code, List<Choice> choices) implements Manageable {
 public Question { choices=List.copyOf(choices); }
 public String getId(){return questionId;}
}
