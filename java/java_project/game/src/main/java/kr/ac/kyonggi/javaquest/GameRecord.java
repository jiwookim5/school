package kr.ac.kyonggi.javaquest;

import java.time.LocalDateTime;

/** One successful mission clear. Records are kept separately from current progress. */
public final class GameRecord {
    private final String missionKey;
    private final int score;
    private final int stars;
    private final LocalDateTime completedAt;

    public GameRecord(String missionKey, int score, int stars, LocalDateTime completedAt) {
        this.missionKey = missionKey;
        this.score = score;
        this.stars = stars;
        this.completedAt = completedAt;
    }

    public String missionKey() {
        return missionKey;
    }

    public int score() {
        return score;
    }

    public int stars() {
        return stars;
    }

    public LocalDateTime completedAt() {
        return completedAt;
    }
}
