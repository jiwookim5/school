package kr.ac.kyonggi.javaquest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/** Timed comparison game: identify the only candidate line that contains a Java error. */
final class BugHunterGamePanel extends GamePanelBase {
    private int secondsLeft = 15;
    private final JLabel timeLabel = Theme.label("15초", 22, Theme.RED);
    private final JLabel status = Theme.label("정상 코드와 다른 줄을 하나 고르세요.", 14, Theme.MUTED);
    private final Timer timer;

    BugHunterGamePanel(JavaQuestFrame frame, LearningService service, Mission mission) {
        super(frame, service, mission);
        addGameHeader("정상 코드와 비교해 버그가 숨은 한 줄을 제한 시간 안에 선택하세요.");

        JPanel game = panelCard();
        game.setLayout(new BorderLayout(18, 16));
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(Theme.heading("버그 후보를 골라라!"), BorderLayout.WEST);
        JPanel timerBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        timerBox.setOpaque(false);
        timerBox.add(Theme.label("TIME", 13, Theme.MUTED));
        timerBox.add(timeLabel);
        title.add(timerBox, BorderLayout.EAST);
        game.add(title, BorderLayout.NORTH);

        JPanel middle = new JPanel(new GridLayout(1, 2, 18, 0));
        middle.setOpaque(false);
        JPanel reference = new JPanel(new BorderLayout(0, 8));
        reference.setOpaque(false);
        reference.add(Theme.label("기준 코드", 14, Theme.PURPLE_DARK), BorderLayout.NORTH);
        JScrollPane codeScroll = new JScrollPane(codeArea(mission.referenceCode(), false));
        codeScroll.setBorder(BorderFactory.createEmptyBorder());
        reference.add(codeScroll, BorderLayout.CENTER);
        middle.add(reference);

        JPanel candidates = new JPanel();
        candidates.setOpaque(false);
        candidates.setLayout(new BoxLayout(candidates, BoxLayout.Y_AXIS));
        JLabel candidateTitle = Theme.label("버그 후보", 14, Theme.PURPLE_DARK);
        candidateTitle.setAlignmentX(LEFT_ALIGNMENT);
        candidates.add(candidateTitle);
        candidates.add(Box.createVerticalStrut(8));
        for (int index = 0; index < mission.bugCandidates().size(); index++) {
            final int candidateIndex = index;
            JButton candidate = Theme.ghostButton("<html><code>" + escape(mission.bugCandidates().get(index)) + "</code></html>");
            candidate.setHorizontalAlignment(SwingConstants.LEFT);
            candidate.setAlignmentX(LEFT_ALIGNMENT);
            candidate.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 48));
            candidate.addActionListener(event -> choose(candidateIndex, candidate));
            candidates.add(candidate);
            candidates.add(Box.createVerticalStrut(9));
        }
        middle.add(candidates);
        game.add(middle, BorderLayout.CENTER);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setOpaque(false);
        footer.add(status);
        game.add(footer, BorderLayout.SOUTH);
        add(game);
        add(Box.createVerticalStrut(14));
        JPanel hint = panelCard();
        hint.add(Theme.label("🐉 기룡이의 힌트  " + mission.hint(), 14, Theme.PURPLE_DARK));
        add(hint);

        timer = new Timer(1000, event -> tick());
        timer.start();
    }

    private void choose(int index, JButton button) {
        if (index == mission.bugAnswerIndex()) {
            int stars = secondsLeft >= 10 ? 3 : secondsLeft >= 5 ? 2 : 1;
            button.setBackground(new Color(225, 247, 233));
            status.setText("정답! 이 줄은 Java 문법 또는 타입 규칙과 맞지 않아요.");
            status.setForeground(Theme.GREEN);
            complete(500 + secondsLeft * 60, stars);
        } else {
            secondsLeft = Math.max(0, secondsLeft - 3);
            updateTime();
            button.setForeground(Theme.RED);
            button.setBorder(BorderFactory.createLineBorder(Theme.RED));
            status.setText("정상 코드예요. 3초가 줄었어요. 다시 비교해 보세요!");
            status.setForeground(Theme.RED);
            if (secondsLeft == 0) {
                timeout();
            }
        }
    }

    private void tick() {
        secondsLeft--;
        updateTime();
        if (secondsLeft <= 0) {
            timeout();
        }
    }

    private void updateTime() {
        timeLabel.setText(Math.max(0, secondsLeft) + "초");
    }

    private void timeout() {
        timer.stop();
        javax.swing.JOptionPane.showMessageDialog(frame, "시간이 끝났어요. 미션 목록에서 다시 도전해 보세요!", "버그 헌터", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        frame.showChapter(mission.chapterNumber());
    }

    @Override
    void stopGame() {
        timer.stop();
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
