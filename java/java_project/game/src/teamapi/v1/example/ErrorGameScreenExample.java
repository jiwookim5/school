package teamapi.v1.example;
import teamapi.v1.*;
import java.io.IOException;
/**
 * 허온(오류 찾기 백엔드)과 이재성(화면)의 연결 예시.
 * 실제 화면의 시작/제출/다음 버튼 Listener에서 같은 순서로 호출한다.
 */
public final class ErrorGameScreenExample {
 private final GameService manager;
 private String gameId;
 public ErrorGameScreenExample(GameService manager){this.manager=manager;}
 public Question start(String userId,int chapterId){
  gameId=manager.startGame(userId,chapterId).gameId();
  return manager.getCurrentQuestion(gameId);
 }
 public String submit(String questionId,String input){
  boolean correct=manager.submitAnswer(gameId,questionId,input);
  return (correct?"정답! ":"오답! ")+manager.getExplanation(gameId);
 }
 /** 반환값이 null이면 finish로 결과를 가져온다. */
 public Question next(){return manager.nextQuestion(gameId);}
 public GameResult finish()throws IOException{return manager.endGame(gameId);}
}
