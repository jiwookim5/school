package kr.ac.kyonggi.javaquest;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.Duration;
import java.time.Instant;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** A typing race where accuracy advances the learner's side of the duel. */
final class TypingGamePanel extends GamePanelBase {
    private final String target;
    private final JTextArea input = codeArea("", true);
    private final JLabel accuracyLabel = Theme.label("정확도 100%", 14, Theme.GREEN);
    private final JLabel timeLabel = Theme.label("00:00", 16, Theme.PURPLE);
    private final JProgressBar learnerBar = new JProgressBar(0, 100);
    private final Timer clock;
    private final Instant startedAt = Instant.now();

    TypingGamePanel(JavaQuestFrame frame, LearningService service, Mission mission) {
        super(frame, service, mission);
        target = normalize(mission.typingCode());
        addGameHeader("표시된 코드를 정확히 입력해 상대를 밀어내세요. 오타가 있으면 전진이 느려집니다.");

        JPanel game = panelCard();
        game.setLayout(new BoxLayout(game, BoxLayout.Y_AXIS));
        JPanel state = new JPanel(new GridLayout(1, 3, 10, 0));
        state.setOpaque(false);
        state.add(stat("나", "입력 중"));
        state.add(stat("상대", "컴퓨터"));
        JPanel time = stat("TIME", "");
        time.add(timeLabel, BorderLayout.SOUTH);
        state.add(time);
        game.add(state);
        game.add(Box.createVerticalStrut(15));

        learnerBar.setStringPainted(true);
        learnerBar.setString("나 0%   |   컴퓨터 50%");
        learnerBar.setForeground(Theme.PURPLE);
        learnerBar.setBackground(new java.awt.Color(237, 232, 250));
        learnerBar.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 28));
        game.add(learnerBar);
        game.add(Box.createVerticalStrut(18));

        JPanel editors = new JPanel(new GridLayout(1, 2, 16, 0));
        editors.setOpaque(false);
        editors.add(editor("입력할 코드", codeArea(mission.typingCode(), false)));
        input.setToolTipText("여기에 코드를 입력하세요");
        input.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { updateRace(); }
            @Override public void removeUpdate(DocumentEvent event) { updateRace(); }
            @Override public void changedUpdate(DocumentEvent event) { updateRace(); }
        });
        editors.add(editor("내 입력", input));
        game.add(editors);
        game.add(Box.createVerticalStrut(12));
        game.add(accuracyLabel);
        add(game);
        add(Box.createVerticalStrut(14));
        JPanel hint = panelCard();
        hint.add(Theme.label("🐉 기룡이의 팁  줄바꿈과 공백도 코드의 일부예요. 천천히 정확하게 입력해 보세요.", 14, Theme.PURPLE_DARK));
        add(hint);

        clock = new Timer(1000, event -> updateClock());
        clock.start();
        javax.swing.SwingUtilities.invokeLater(input::requestFocusInWindow);
    }

    private JPanel stat(String label, String value) {
        JPanel panel = Theme.card();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.LINE), Theme.padding(10, 12, 10, 12)));
        panel.add(Theme.label(label, 12, Theme.MUTED), BorderLayout.NORTH);
        panel.add(Theme.label(value, 15, Theme.INK), BorderLayout.CENTER);
        return panel;
    }

    private JPanel editor(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(Theme.label(title, 14, Theme.PURPLE_DARK), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void updateRace() {
        String typed = normalize(input.getText());
        int matchingPrefix = 0;
        while (matchingPrefix < typed.length() && matchingPrefix < target.length()
                && typed.charAt(matchingPrefix) == target.charAt(matchingPrefix)) {
            matchingPrefix++;
        }
        int progress = target.isEmpty() ? 0 : Math.min(100, matchingPrefix * 100 / target.length());
        int accuracy = typed.isEmpty() ? 100 : matchingPrefix * 100 / typed.length();
        learnerBar.setValue(progress);
        learnerBar.setString("나 " + progress + "%   |   컴퓨터 50%");
        accuracyLabel.setText("정확도 " + accuracy + "% · " + matchingPrefix + " / " + target.length() + " 글자 일치");
        accuracyLabel.setForeground(accuracy >= 90 ? Theme.GREEN : Theme.RED);
        if (typed.equals(target)) {
            long seconds = Duration.between(startedAt, Instant.now()).toSeconds();
            int stars = seconds <= 20 ? 3 : seconds <= 45 ? 2 : 1;
            complete(Math.max(250, 1400 - (int) seconds * 20), stars);
        }
    }

    private void updateClock() {
        long seconds = Duration.between(startedAt, Instant.now()).toSeconds();
        timeLabel.setText("%02d:%02d".formatted(seconds / 60, seconds % 60));
    }

    @Override
    void stopGame() {
        clock.stop();
    }

    private static String normalize(String value) {
        return value.replace("\r\n", "\n").replace('\r', '\n');
    }
}
