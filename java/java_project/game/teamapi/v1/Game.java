package teamapi.v1;
/**
 * 게임 실행의 읽기 전용 스냅샷. gameId는 플레이마다 새로 생성하는 고유 문자열.
 * currentQuestionIndex는 0부터. totalPossibleScore는 전체 원점수 만점.
 * currentScore는 현재 원점수. 프론트에서 점수/인덱스를 직접 변경할 수 없다.
 */
public record Game(String gameId, String userId, int chapterId, GameType gameType,
                   GameStatus status, int currentQuestionIndex, Question currentQuestion,
                   int currentScore, int totalPossibleScore,
                   int correctCount, int answeredCount, int totalQuestionCount)
                   implements Manageable {
 public String getId(){return gameId;}
}
