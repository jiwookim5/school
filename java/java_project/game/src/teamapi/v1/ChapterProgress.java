package teamapi.v1;
/** 잠금과 최고 기록은 사용자별로 관리한다. 재도전으로 최고 별을 낮추지 않는다. */
public record ChapterProgress(String userId,int chapterId,boolean unlocked,
                              boolean cleared,int bestScore,int bestStars) {}
