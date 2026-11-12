package teamapi.v1;
import java.util.List;
import java.io.IOException;
/** 메인 화면에서 사용할 챕터 목록과 사용자별 진행 상태. */
public interface ChapterService {
 List<Chapter> getChapters() throws IOException;
 List<ChapterProgress> getProgress(String userId) throws IOException;
}
