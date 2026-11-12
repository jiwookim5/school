package kr.ac.kyonggi.javaquest;

import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Content and rule data for one game mission. */
public final class Mission {
    private final int chapterNumber;
    private final int number;
    private final GameType type;
    private final String hint;
    private final LinkedHashMap<String, String> matchingPairs;
    private final String typingCode;
    private final List<String> stackBlocks;
    private final String referenceCode;
    private final List<String> bugCandidates;
    private final int bugAnswerIndex;

    public Mission(
            int chapterNumber,
            int number,
            GameType type,
            String hint,
            Map<String, String> matchingPairs,
            String typingCode,
            List<String> stackBlocks,
            String referenceCode,
            List<String> bugCandidates,
            int bugAnswerIndex) {
        this.chapterNumber = chapterNumber;
        this.number = number;
        this.type = type;
        this.hint = hint;
        this.matchingPairs = new LinkedHashMap<>(matchingPairs);
        this.typingCode = typingCode;
        this.stackBlocks = List.copyOf(stackBlocks);
        this.referenceCode = referenceCode;
        this.bugCandidates = List.copyOf(bugCandidates);
        this.bugAnswerIndex = bugAnswerIndex;
    }

    public int chapterNumber() {
        return chapterNumber;
    }

    public int number() {
        return number;
    }

    public GameType type() {
        return type;
    }

    public String hint() {
        return hint;
    }

    public Map<String, String> matchingPairs() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(matchingPairs));
    }

    public String typingCode() {
        return typingCode;
    }

    public List<String> stackBlocks() {
        return stackBlocks;
    }

    public String referenceCode() {
        return referenceCode;
    }

    public List<String> bugCandidates() {
        return bugCandidates;
    }

    public int bugAnswerIndex() {
        return bugAnswerIndex;
    }

    public String key() {
        return chapterNumber + "-" + number;
    }
}
