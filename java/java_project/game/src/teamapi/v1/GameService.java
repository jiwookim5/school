package teamapi.v1;
import java.io.IOException;
/**
 * 각 게임의 Manager가 구현할 공통 규격 v1.
 * Swing은 이 인터페이스를 생성자로 주입받는다.
 * 구현체는 사용자 세션/챕터 정보/결과 저장 담당을 주입받아 사용한다.
 */
public interface GameService {
 /**
  * 로그인된 사용자와 일치하고 챕터가 해금된 경우에만 시작.
  * gameId는 매 실행 새로 생성. 이미 진행 중인 게임이 있으면 거부.
  */
 Game startGame(String userId,int chapterId);
 /**
  * 최신 실행 스냅샷. 알 수 없는 gameId는 IllegalArgumentException.
  * 시간 제한 게임은 이 호출에서도 실제 경과 시간과 종료 조건을 갱신한다.
  * 시간 종료 시 화면은 FINISHED를 확인해 endGame을 호출한다.
  */
 Game getGame(String gameId);
 /** 현재 문제 조회만 수행한다. 문제를 넘기지 않는다. 종료 뒤에는 null. */
 Question getCurrentQuestion(String gameId);
 /**
  * 정답 true, 오답 false. 채점 후 ANSWERED로 바꾸고 인덱스는 유지한다.
  * 중복 제출/제출 전 다음 이동은 IllegalStateException.
  * questionId가 현재와 다르면 IllegalArgumentException. 오래된 화면의 제출을 방지한다.
  * OX: O/X, 선택형: 보기 ID, 주관식: 입력 코드, 카드형: 선택한 상대 카드 ID.
  */
 boolean submitAnswer(String gameId,String questionId,String userInput);
 /** 제출 후에만 해설 제공. 제출 전에는 IllegalStateException. */
 String getExplanation(String gameId);
 /**
  * ANSWERED에서만 다음 문제로 이동. 다음 문제가 없으면 FINISHED로 바꾸고 null.
  * 일반 문항형은 마지막 문제까지 채점한다.
  * 시간초과/체력소진은 남은 문제가 있어도 백엔드가 FINISHED로 처리할 수 있다.
  */
 Question nextQuestion(String gameId);
 /**
  * FINISHED 결과를 저장하고 누적 점수/진행을 반영한 뒤 반환.
  * gameId 기준 중복 지급을 막는다. 저장 실패 시 IOException, 같은 ID로 재시도 가능.
  * 재호출은 동일 결과 반환. 진행 중 호출은 IllegalStateException.
  */
 GameResult endGame(String gameId) throws IOException;
 /**
  * 중도 종료 처리 및 결과 저장. 보상/클리어/누적 정답률에는 반영하지 않는다.
  * 진행 시간과 풀이 기록은 보존. 같은 gameId 재호출은 같은 결과 반환.
  */
 GameResult abortGame(String gameId) throws IOException;
}
