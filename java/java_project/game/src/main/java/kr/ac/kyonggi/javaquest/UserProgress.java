package kr.ac.kyonggi.javaquest;

import java.util.HashMap;
import java.util.Map;

/** Current completion and best-star state per mission. */
public final class UserProgress {
    private final Map<String, Integer> bestStarsByMission = new HashMap<>();

    public boolean isCompleted(Mission mission) {
        return bestStarsByMission.containsKey(mission.key());
    }

    public int stars(Mission mission) {
        return bestStarsByMission.getOrDefault(mission.key(), 0);
    }

    public void markCompleted(Mission mission, int stars) {
        bestStarsByMission.merge(mission.key(), Math.max(1, Math.min(3, stars)), Math::max);
    }

    public void clear() {
        bestStarsByMission.clear();
    }
}
