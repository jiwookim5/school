package teamapi.v1;
import java.io.IOException;
/** 회원 정보 파일 I/O는 구현체가 담당한다. 화면은 텍스트 파일을 직접 다루지 않는다. */
public interface UserService {
 /**
  * 성공 true, 중복 ID false. 가입 후 자동 로그인하지 않는다.
  * 처음에는 name을 id로 설정한다. 비밀번호는 솔트와 안전한 해시로만 저장한다.
  */
 boolean signUp(String id,String password) throws IOException;
 /** 성공 시 현재 사용자 설정 후 User 반환. 불일치 시 null, 기존 로그인도 해제. */
 User login(String id,String password) throws IOException;
 /** 로그인 전에는 null. 비밀번호를 제외한 최신 정보의 스냅샷. */
 User getCurrentUser();
 /** 진행 중 게임이 있으면 먼저 해당 게임을 중단한 다음 호출한다. */
 void logout();
}
