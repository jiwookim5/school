package kr.ac.kyonggi.javaquest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/** Applies unlock rules and stores progress and records for the current user. */
public final class LearningService {
    private final User user;
    private final List<Chapter> chapters;
    private final UserProgress progress = new UserProgress();
    private final List<GameRecord> records = new ArrayList<>();
    private final Path progressFile = Path.of(System.getProperty("user.home"), ".javaquest", "progress.properties");

    public LearningService(User user, List<Chapter> chapters) {
        this.user = user;
        this.chapters = List.copyOf(chapters);
    }

    public User user() {
        return user;
    }

    public List<Chapter> chapters() {
        return chapters;
    }

    public boolean isChapterUnlocked(Chapter chapter) {
        if (chapter.number() == 1) {
            return true;
        }
        return chapters.stream()
                .filter(item -> item.number() == chapter.number() - 1)
                .findFirst()
                .map(this::isChapterComplete)
                .orElse(false);
    }

    public boolean isMissionUnlocked(Mission mission) {
        Chapter chapter = chapter(mission.chapterNumber()).orElseThrow();
        if (!isChapterUnlocked(chapter)) {
            return false;
        }
        return mission.number() == 1 || progress.isCompleted(chapter.missions().get(mission.number() - 2));
    }

    public boolean isCompleted(Mission mission) {
        return progress.isCompleted(mission);
    }

    public int stars(Mission mission) {
        return progress.stars(mission);
    }

    public int completedCount(Chapter chapter) {
        return (int) chapter.missions().stream().filter(progress::isCompleted).count();
    }

    public boolean isChapterComplete(Chapter chapter) {
        return completedCount(chapter) == chapter.missions().size();
    }

    public int completedMissionCount() {
        return (int) chapters.stream().flatMap(chapter -> chapter.missions().stream()).filter(progress::isCompleted).count();
    }

    public int totalStars() {
        return chapters.stream().flatMap(chapter -> chapter.missions().stream()).mapToInt(progress::stars).sum();
    }

    public int totalMissionCount() {
        return chapters.stream().mapToInt(chapter -> chapter.missions().size()).sum();
    }

    public int progressPercent() {
        return Math.round(100f * completedMissionCount() / totalMissionCount());
    }

    public Optional<Chapter> currentChapter() {
        return chapters.stream()
                .filter(this::isChapterUnlocked)
                .filter(chapter -> !isChapterComplete(chapter))
                .max(Comparator.comparingInt(Chapter::number))
                .or(() -> chapters.stream().max(Comparator.comparingInt(Chapter::number)));
    }

    public List<GameRecord> records() {
        return List.copyOf(records);
    }

    /** Loads local progress from a text properties file; no database is used. */
    public boolean loadLocalProgress() {
        if (!Files.isRegularFile(progressFile)) {
            return false;
        }
        Properties saved = new Properties();
        try (InputStream input = Files.newInputStream(progressFile)) {
            saved.load(input);
            progress.clear();
            for (Chapter chapter : chapters) {
                for (Mission mission : chapter.missions()) {
                    String value = saved.getProperty("mission." + mission.key());
                    if (value != null) {
                        progress.markCompleted(mission, Integer.parseInt(value));
                    }
                }
            }
            records.clear();
            int recordCount = Integer.parseInt(saved.getProperty("record.count", "0"));
            for (int index = 0; index < recordCount; index++) {
                String prefix = "record." + index + ".";
                try {
                    records.add(new GameRecord(
                            saved.getProperty(prefix + "mission", ""),
                            Integer.parseInt(saved.getProperty(prefix + "score", "0")),
                            Integer.parseInt(saved.getProperty(prefix + "stars", "1")),
                            LocalDateTime.parse(saved.getProperty(prefix + "time"))));
                } catch (RuntimeException ignored) {
                    // Skip an incomplete record and continue loading valid local data.
                }
            }
            return true;
        } catch (IOException | RuntimeException exception) {
            System.err.println("진행 파일을 읽지 못했습니다: " + exception.getMessage());
            return false;
        }
    }

    private void saveLocalProgress() {
        Properties saved = new Properties();
        for (Chapter chapter : chapters) {
            for (Mission mission : chapter.missions()) {
                int stars = progress.stars(mission);
                if (stars > 0) {
                    saved.setProperty("mission." + mission.key(), Integer.toString(stars));
                }
            }
        }
        saved.setProperty("record.count", Integer.toString(records.size()));
        for (int index = 0; index < records.size(); index++) {
            GameRecord record = records.get(index);
            String prefix = "record." + index + ".";
            saved.setProperty(prefix + "mission", record.missionKey());
            saved.setProperty(prefix + "score", Integer.toString(record.score()));
            saved.setProperty(prefix + "stars", Integer.toString(record.stars()));
            saved.setProperty(prefix + "time", record.completedAt().toString());
        }
        try {
            Files.createDirectories(progressFile.getParent());
            try (OutputStream output = Files.newOutputStream(progressFile)) {
                saved.store(output, "JAVA QUEST local progress");
            }
        } catch (IOException exception) {
            System.err.println("진행 파일을 저장하지 못했습니다: " + exception.getMessage());
        }
    }

    public void complete(Mission mission, int score, int stars) {
        progress.markCompleted(mission, stars);
        records.add(new GameRecord(mission.key(), score, stars, LocalDateTime.now()));
        saveLocalProgress();
    }

    public void resetProgress() {
        progress.clear();
        records.clear();
        saveLocalProgress();
    }

    private Optional<Chapter> chapter(int number) {
        return chapters.stream().filter(chapter -> chapter.number() == number).findFirst();
    }
}
