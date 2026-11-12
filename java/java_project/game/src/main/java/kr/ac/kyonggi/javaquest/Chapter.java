package kr.ac.kyonggi.javaquest;

import java.util.List;

/** A Java concept chapter containing the four required game missions. */
public final class Chapter {
    private final int number;
    private final String title;
    private final String summary;
    private final List<Mission> missions;

    public Chapter(int number, String title, String summary, List<Mission> missions) {
        this.number = number;
        this.title = title;
        this.summary = summary;
        this.missions = List.copyOf(missions);
    }

    public int number() {
        return number;
    }

    public String title() {
        return title;
    }

    public String summary() {
        return summary;
    }

    public List<Mission> missions() {
        return missions;
    }
}
