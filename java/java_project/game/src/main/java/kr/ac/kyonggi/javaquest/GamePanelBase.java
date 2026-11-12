package kr.ac.kyonggi.javaquest;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/** Shared game-screen shell and completion behavior. */
abstract class GamePanelBase extends JPanel {
    protected final JavaQuestFrame frame;
    protected final LearningService service;
    protected final Mission mission;
    private boolean finished;

    GamePanelBase(JavaQuestFrame frame, LearningService service, Mission mission) {
        this.frame = frame;
        this.service = service;
        this.mission = mission;
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.padding(24, 34, 30, 34));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    protected void addGameHeader(String instruction) {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);
        JButton back = Theme.ghostButton("← 월드맵");
        back.addActionListener(event -> frame.showMap());
        header.add(back, BorderLayout.WEST);
        JPanel words = new JPanel();
        words.setOpaque(false);
        words.setLayout(new BoxLayout(words, BoxLayout.Y_AXIS));
        JLabel overline = Theme.label("JAVA FOREST · STAGE %02d".formatted(mission.number()), 13, Theme.PURPLE);
        overline.setAlignmentX(Component.LEFT_ALIGNMENT);
        words.add(overline);
        words.add(Box.createVerticalStrut(3));
        words.add(Theme.title(mission.type().title()));
        words.add(Box.createVerticalStrut(4));
        words.add(Theme.label(instruction, 14, Theme.MUTED));
        header.add(words, BorderLayout.CENTER);
        add(header);
        add(Box.createVerticalStrut(18));
    }

    protected JPanel panelCard() {
        return Theme.card();
    }

    protected JTextArea codeArea(String code, boolean editable) {
        JTextArea area = new JTextArea(code);
        area.setEditable(editable);
        area.setLineWrap(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        area.setForeground(Theme.INK);
        area.setBackground(new java.awt.Color(247, 248, 252));
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.LINE), Theme.padding(12, 14, 12, 14)));
        return area;
    }

    protected void complete(int score, int stars) {
        if (finished) {
            return;
        }
        finished = true;
        stopGame();
        service.complete(mission, score, stars);
        String starText = "★".repeat(stars) + "☆".repeat(3 - stars);
        JOptionPane.showMessageDialog(frame,
                "미션 완료! " + starText + "\n점수 " + score + "점을 얻었어요.",
                "기룡이의 축하", JOptionPane.INFORMATION_MESSAGE);
        frame.showChapter(mission.chapterNumber());
    }

    protected void completeWithChallenge(int score, int stars, boolean wasChallenge) {
        if (finished) {
            return;
        }
        finished = true;
        stopGame();
        service.complete(mission, score, stars);
        String starText = "★".repeat(stars) + "☆".repeat(3 - stars);
        String[] options = wasChallenge
                ? new String[]{"홈으로 가기", "도전 다시 하기"}
                : new String[]{"홈으로 가기", "도전 단계 도전하기"};
        int choice = JOptionPane.showOptionDialog(frame,
                "미션 완료! " + starText + "\n점수 " + score + "점을 얻었어요.",
                "기룡이의 축하", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
        if (choice == 1) {
            frame.showGame(mission, true);
        } else {
            frame.showMap();
        }
    }

    /** Timer-based games override this to release their timers on navigation. */
    void stopGame() {
    }
}
