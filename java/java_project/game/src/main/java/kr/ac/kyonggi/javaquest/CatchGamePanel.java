package kr.ac.kyonggi.javaquest;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FlowLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/** Stage 3: guide Giryong to catch Java code lines in order. */
final class CatchGamePanel extends GamePanelBase {
    private static final int STARTING_HEARTS = 3;
    private final boolean challenge;
    private final int arenaWidth;
    private final int arenaHeight;
    private final List<String> answers;
    private final CatchArena arena;
    private final JLabel targetLabel = Theme.label("", 18, Theme.PURPLE_DARK);
    private final JLabel heartsLabel = Theme.label("", 17, Theme.RED);
    private final JLabel progressLabel = Theme.label("", 14, Theme.MUTED);
    private final JLabel feedbackLabel = Theme.label("← → 방향키로 움직여 정답 코드를 순서대로 담으세요.", 14, Theme.MUTED);
    private final Timer gameTimer;
    private int targetIndex;
    private int hearts = STARTING_HEARTS;
    private int score;
    private boolean over;
    private boolean damageFlash;

    CatchGamePanel(JavaQuestFrame frame, LearningService service, Mission mission, boolean challenge) {
        super(frame, service, mission);
        this.challenge = challenge;
        arenaWidth = Math.max(900, Math.min(1600, frame.getWidth() - 96));
        arenaHeight = Math.max(360, frame.getHeight() - 320);
        answers = codeLines(mission.chapterNumber(), challenge);
        arena = new CatchArena();
        addGameHeader("기룡이를 좌우로 움직여 정답 Java 코드 줄을 순서대로 담으세요. 오답은 피해야 해요.");

        JPanel hud = panelCard();
        hud.setLayout(new BorderLayout(18, 0));
        JPanel objective = new JPanel();
        objective.setOpaque(false);
        objective.setLayout(new BoxLayout(objective, BoxLayout.Y_AXIS));
        objective.add(Theme.label("지금 받아야 할 코드 줄  ·  " + (challenge ? "도전" : "쉬움"), 13, Theme.MUTED));
        objective.add(Box.createVerticalStrut(4));
        targetLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        objective.add(targetLabel);
        hud.add(objective, BorderLayout.CENTER);
        JPanel state = new JPanel();
        state.setOpaque(false);
        state.setLayout(new BoxLayout(state, BoxLayout.Y_AXIS));
        heartsLabel.setAlignmentX(RIGHT_ALIGNMENT);
        progressLabel.setAlignmentX(RIGHT_ALIGNMENT);
        state.add(heartsLabel);
        state.add(Box.createVerticalStrut(4));
        state.add(progressLabel);
        hud.add(state, BorderLayout.EAST);
        add(hud);
        add(Box.createVerticalStrut(10));

        JPanel difficulty = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        difficulty.setOpaque(false);
        difficulty.setAlignmentX(LEFT_ALIGNMENT);
        JButton easyButton = difficultyButton("쉬움 · 5줄 · 느린 낙하", !challenge);
        JButton challengeButton = difficultyButton("도전 · 8줄 · 빠른 낙하", challenge);
        easyButton.addActionListener(event -> { if (challenge) frame.showGame(mission, false); });
        challengeButton.addActionListener(event -> { if (!challenge) frame.showGame(mission, true); });
        difficulty.add(easyButton);
        difficulty.add(challengeButton);
        add(difficulty);
        add(Box.createVerticalStrut(6));

        arena.setAlignmentX(CENTER_ALIGNMENT);
        add(arena);
        add(Box.createVerticalStrut(8));
        feedbackLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(feedbackLabel);
        setBorder(Theme.padding(12, 30, 12, 30));

        installKeyboardControls();
        refreshHud();
        gameTimer = new Timer(35, event -> tick());
        gameTimer.start();
    }

    private static List<String> codeLines(int chapter, boolean challenge) {
        String[] lines = switch (chapter) {
            case 2 -> challenge
                    ? new String[]{"int score = 75;", "if (score >= 60) {", "    for (int bonus = 0; bonus < 2; bonus++) {", "        System.out.println(\"PASS\");", "    }", "} else {", "    System.out.println(\"RETRY\");", "}"}
                    : new String[]{"int score = 75;", "if (score >= 60) {", "    System.out.println(\"PASS\");", "} else {", "    System.out.println(\"RETRY\");"};
            case 3 -> challenge
                    ? new String[]{"class Calculator {", "    int sum(int a, int b) {", "        int result = a + b;", "        return result;", "    }", "    void printSum() {", "        System.out.println(sum(3, 5));", "    }"}
                    : new String[]{"int sum(int a, int b) {", "    int result = a + b;", "    return result;", "}", "System.out.println(sum(3, 5));"};
            case 4 -> challenge
                    ? new String[]{"class ScoreBoard {", "    int[] scores = {90, 80, 70};", "    int total = 0;", "    for (int score : scores) {", "        total += score;", "    }", "    System.out.println(scores.length);", "}"}
                    : new String[]{"int[] scores = {90, 80, 70};", "int total = 0;", "for (int score : scores) {", "    total += score;", "}"};
            case 5 -> challenge
                    ? new String[]{"class Hero {", "    private int hp = 100;", "    String name = \"Giryong\";", "    void attack() {", "        hp -= 10;", "        System.out.println(name);", "    }", "}"}
                    : new String[]{"class Hero {", "    String name = \"Giryong\";", "    void attack() {", "        System.out.println(\"Attack!\");", "    }"};
            case 6 -> challenge
                    ? new String[]{"package quest.java;", "import java.util.ArrayList;", "public class Quest {", "    ArrayList<String> quests = new ArrayList<>();", "    void addQuest() {", "        quests.add(\"Java\");", "    }", "}"}
                    : new String[]{"package quest.java;", "import java.util.ArrayList;", "public class Quest {", "    String name = \"Java\";", "}"};
            default -> challenge
                    ? new String[]{"class Hero {", "    private int hp = 100;", "    String name = \"Giryong\";", "    void attack() {", "        hp -= 10;", "        System.out.println(name);", "    }", "}"}
                    : new String[]{"class Hero {", "    void attack() {", "        System.out.println(\"Attack!\");", "    }", "}"};
        };
        return List.of(lines);
    }

    private void installKeyboardControls() {
        InputMap input = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        input.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        input.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        getActionMap().put("moveLeft", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { arena.movePlayer(-1); }
        });
        getActionMap().put("moveRight", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { arena.movePlayer(1); }
        });
    }

    private JButton difficultyButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 10));
        button.setForeground(selected ? Theme.PURPLE_DARK : Theme.MUTED);
        button.setBackground(selected ? new Color(241, 239, 255) : Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selected ? new Color(202, 194, 245) : Theme.LINE),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        return button;
    }

    private void tick() {
        if (!over) {
            arena.advance();
        }
    }

    private void caught(FallingCard card) {
        if (card.targetIndex == targetIndex) {
            score += 300;
            targetIndex++;
            arena.targetInFlight = false;
            feedbackLabel.setText("정답 코드 줄을 담았어요! 다음 줄을 이어서 찾으세요.");
            feedbackLabel.setForeground(Theme.GREEN);
            if (targetIndex == answers.size()) {
                over = true;
                refreshHud();
                completeWithChallenge(score + hearts * 100, challenge ? 2 : 1, challenge);
                return;
            }
        } else {
            loseHeart("오답 코드 줄을 받았어요. 정답을 순서대로 찾아야 해요.");
        }
        refreshHud();
    }

    private void missed(FallingCard card) {
        if (card.targetIndex == targetIndex) {
            arena.targetInFlight = false;
            loseHeart("정답 코드를 놓쳤어요. 같은 줄이 다시 나타납니다.");
        }
    }

    private void loseHeart(String message) {
        if (over) {
            return;
        }
        hearts--;
        damageFlash = true;
        arena.damageFlash = true;
        frame.flashDamage();
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(Theme.RED);
        Timer clearFlash = new Timer(380, event -> {
            damageFlash = false;
            arena.damageFlash = false;
            arena.repaint();
            ((Timer) event.getSource()).stop();
        });
        clearFlash.setRepeats(false);
        clearFlash.start();
        if (hearts <= 0) {
            over = true;
            gameTimer.stop();
            javax.swing.JOptionPane.showMessageDialog(frame,
                    "하트를 모두 잃었어요. 미션 목록에서 다시 도전해 보세요!",
                    "정답 담기", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            frame.showMap();
        }
        refreshHud();
    }

    private void refreshHud() {
        String answer = targetIndex < answers.size() ? answers.get(targetIndex) : "모든 정답 수집 완료";
        targetLabel.setText("<html><code>" + escape(answer) + "</code></html>");
        heartsLabel.setText("❤".repeat(Math.max(0, hearts)) + "♡".repeat(Math.max(0, STARTING_HEARTS - hearts)));
        progressLabel.setText("코드 " + targetIndex + " / " + answers.size() + " · " + score + "점");
        arena.repaint();
    }

    @Override
    void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private final class CatchArena extends JPanel {
        private static final int PLAYER_WIDTH = 82;
        private static final int CARD_HEIGHT = 38;
        private final List<FallingCard> cards = new ArrayList<>();
        private final Random random = new Random();
        private int playerX = arenaWidth / 2 - PLAYER_WIDTH / 2;
        private int spawnTicks;
        private boolean targetInFlight;
        private boolean damageFlash;

        CatchArena() {
            setPreferredSize(new java.awt.Dimension(arenaWidth, arenaHeight));
            setMaximumSize(new java.awt.Dimension(arenaWidth, arenaHeight));
            setBackground(new Color(225, 245, 255));
            setBorder(BorderFactory.createLineBorder(new Color(116, 199, 232), 2));
        }

        void movePlayer(int direction) {
            if (over) return;
            playerX = Math.max(12, Math.min(arenaWidth - PLAYER_WIDTH - 12, playerX + direction * 30));
            repaint();
        }

        void advance() {
            spawnTicks++;
            if (spawnTicks >= (challenge ? 25 : 34)) {
                spawnCard();
                spawnTicks = 0;
            }
            for (int index = cards.size() - 1; index >= 0; index--) {
                FallingCard card = cards.get(index);
                card.y += challenge ? (card.targetIndex == targetIndex ? 4.5 : 4.2) : (card.targetIndex == targetIndex ? 3.4 : 3.1);
                if (card.y >= arenaHeight - 80 && overlapsPlayer(card)) {
                    cards.remove(index);
                    caught(card);
                    if (over) break;
                } else if (card.y > arenaHeight + CARD_HEIGHT) {
                    cards.remove(index);
                    missed(card);
                }
            }
            repaint();
        }

        private void spawnCard() {
            if (targetIndex >= answers.size()) return;
            boolean isTarget = !targetInFlight && random.nextInt(100) < (challenge ? 54 : 48);
            int lineIndex = -1;
            String text;
            if (isTarget) {
                lineIndex = targetIndex;
                text = answers.get(targetIndex);
                targetInFlight = true;
            } else {
                text = wrongCardText();
            }
            int width = Math.max(150, Math.min(460, 36 + text.length() * 8));
            int x = 12 + random.nextInt(Math.max(1, arenaWidth - width - 24));
            cards.add(new FallingCard(text, lineIndex, x, -CARD_HEIGHT, width));
        }

        private String wrongCardText() {
            String[] distractors = {
                "return a - b;", "if (ready = true)", "String score = 10;",
                "for (int i = 0; i <= n; i++)", "System.out.printl(\"Java\");",
                "public static void main(String args)", "int[] values = new int[-1];",
                "package = quest.java;", "total = total + score;"
            };
            return distractors[random.nextInt(distractors.length)];
        }

        private boolean overlapsPlayer(FallingCard card) {
            int center = card.x + card.width / 2;
            return card.y + CARD_HEIGHT >= arenaHeight - 100
                    && center >= playerX - 8 && center <= playerX + PLAYER_WIDTH + 8;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            paintBackdrop(g);
            paintCodeStack(g);
            for (FallingCard card : cards) paintCard(g, card);
            paintPlayer(g);
            if (damageFlash) {
                g.setColor(new Color(230, 55, 55, 90));
                g.setStroke(new BasicStroke(9));
                g.drawRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
            }
            g.dispose();
        }

        private void paintBackdrop(Graphics2D g) {
            g.setColor(new Color(255, 255, 255, 180));
            g.fillOval(85, 54, 105, 31);
            g.fillOval(125, 36, 82, 48);
            g.fillOval(690, 80, 112, 32);
            g.fillOval(748, 57, 72, 50);
            g.setColor(new Color(198, 232, 247));
            g.fillRect(0, arenaHeight - 29, getWidth(), 29);
        }

        private void paintCodeStack(Graphics2D g) {
            int width = 440;
            int height = 168;
            g.setColor(new Color(255, 255, 255, 228));
            g.fillRoundRect(16, 15, width, height, 18, 18);
            g.setColor(new Color(139, 186, 211));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(16, 15, width, height, 18, 18);
            g.setColor(Theme.PURPLE_DARK);
            g.setFont(new Font("Malgun Gothic", Font.BOLD, 13));
            g.drawString("모은 코드", 31, 39);
            g.setColor(Theme.INK);
            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            for (int index = 0; index < targetIndex; index++) {
                g.drawString((index + 1) + ". " + shorten(answers.get(index), 49), 31, 62 + index * 17);
            }
            if (targetIndex == 0) {
                g.setColor(Theme.MUTED);
                g.drawString("정답 줄을 담으면 여기에 순서대로 쌓여요.", 31, 65);
            }
        }

        private void paintCard(Graphics2D g, FallingCard card) {
            int y = (int) card.y;
            boolean correct = card.targetIndex == targetIndex;
            Color border = correct ? new Color(32, 146, 202) : new Color(232, 137, 111);
            g.setColor(new Color(255, 255, 255, 245));
            g.fillRoundRect(card.x, y, card.width, CARD_HEIGHT, 13, 13);
            g.setColor(border);
            g.setStroke(new BasicStroke(correct ? 3 : 2));
            g.drawRoundRect(card.x, y, card.width, CARD_HEIGHT, 13, 13);
            g.setColor(Theme.INK);
            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            String text = shorten(card.text, Math.max(20, (card.width - 24) / 8));
            g.drawString(text, card.x + 12, y + 24);
        }

        private void paintPlayer(Graphics2D g) {
            int y = arenaHeight - 101;
            Theme.drawGiryong(g, playerX + 3, y - 8, PLAYER_WIDTH - 6, 76);
            g.setColor(new Color(155, 98, 42));
            g.fillRoundRect(playerX - 2, y + 57, PLAYER_WIDTH + 4, 22, 8, 8);
            g.setColor(new Color(255, 220, 116));
            g.fillOval(playerX + 31, y + 61, 18, 13);
        }

        private String shorten(String text, int maxLength) {
            return text.length() <= maxLength ? text : text.substring(0, maxLength - 1) + "…";
        }
    }

    private static final class FallingCard {
        private final String text;
        private final int targetIndex;
        private final int x;
        private final int width;
        private double y;

        FallingCard(String text, int targetIndex, int x, double y, int width) {
            this.text = text;
            this.targetIndex = targetIndex;
            this.x = x;
            this.y = y;
            this.width = width;
        }
    }
}
