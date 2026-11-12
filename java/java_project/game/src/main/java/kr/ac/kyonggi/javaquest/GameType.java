package kr.ac.kyonggi.javaquest;

public enum GameType {
    MATCHING("개념 카드", "용어와 설명 카드를 뒤집어 짝을 맞춰요"),
    TYPING("오류 수정", "Java 코드의 오류를 찾아 고쳐요"),
    STACK("정답 수집", "기룡이를 움직여 Java 코드 줄을 순서대로 받아요"),
    HAND_CODING("코딩 챌린지", "Java 코드를 직접 작성하여 문제를 풀어요");

    private final String title;
    private final String description;

    GameType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }
}
