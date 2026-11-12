package teamapi.v1;
/**
 * 종료 시 백엔드가 확정한 결과. finalScore는 원점수를 100점으로 환산한 정수.
 * stars: 정상 종료 시 80 이상 3개, 50 이상 2개, 30 이상 1개, 그 미만 0개.
 * 중도 종료는 점수 기록을 보존하되 stars=0, cleared=false.
 * cleared는 별 1개 이상이면서 중도 종료가 아닌 경우.
 */
public record GameResult(String gameId, String userId, int chapterId, GameType gameType,
                         boolean cleared, boolean aborted, int finalScore,
                         int correctCount, int answeredCount, int totalQuestionCount,
                         int stars, long elapsedMillis) {
 public double accuracyPercent(){return totalQuestionCount==0?0:100.0*correctCount/totalQuestionCount;}
}
