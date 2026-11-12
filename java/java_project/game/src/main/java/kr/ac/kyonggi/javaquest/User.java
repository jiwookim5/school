package kr.ac.kyonggi.javaquest;

/** Learner profile shown on the home screen. */
public final class User {
    private final String nickname;
    private final int streakDays;

    public User(String nickname, int streakDays) {
        this.nickname = nickname;
        this.streakDays = streakDays;
    }

    public String nickname() {
        return nickname;
    }

    public int streakDays() {
        return streakDays;
    }
}
