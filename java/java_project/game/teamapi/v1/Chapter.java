package teamapi.v1;
/** chapterId는 고유 번호, order는 화면에 배치할 순서(1부터)이다. */
public record Chapter(int chapterId, String title, String content, int order,
                      GameType gameType) implements Manageable {
 public String getId(){return Integer.toString(chapterId);}
}
