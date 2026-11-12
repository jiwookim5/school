package kr.ac.kyonggi.javaquest;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

/** Memory-card game that pairs Java code with the concept it represents. */
final class MatchingGamePanel extends GamePanelBase {
    private final boolean challenge;
    private final List<Card> deck = new ArrayList<>();
    private final List<JButton> buttons = new ArrayList<>();
    private final JLabel feedback = Theme.label("카드를 두 장씩 뒤집어 Java 개념의 짝을 찾아보세요.", 14, Theme.MUTED);
    private final JLabel progress = Theme.label("찾은 짝 0 / 2", 14, Theme.PURPLE_DARK);
    private Card firstCard;
    private JButton firstButton;
    private boolean locked;
    private int matched;
    private Timer mismatchTimer;

    MatchingGamePanel(JavaQuestFrame frame, LearningService service, Mission mission, boolean challenge) {
        super(frame, service, mission);
        this.challenge = challenge;
        List<Map.Entry<String, String>> concepts = new ArrayList<>(mission.matchingPairs().entrySet());
        int pairLimit = challenge ? concepts.size() : Math.min(2, concepts.size());
        for (int index = 0; index < pairLimit; index++) {
            Map.Entry<String, String> pair = concepts.get(index);
            deck.add(new Card(pair.getKey(), pair.getKey(), true));
            deck.add(new Card(pair.getKey(), pair.getValue(), false));
        }
        Collections.shuffle(deck);

        addGameHeader("Java 코드와 개념 카드를 기억해 짝을 맞추세요.");
        JPanel title = panelCard();
        title.setLayout(new BorderLayout(12, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        JPanel titleWords = new JPanel();
        titleWords.setOpaque(false);
        titleWords.setLayout(new BoxLayout(titleWords, BoxLayout.Y_AXIS));
        titleWords.add(Theme.heading("개념 카드 짝 맞추기"));
        titleWords.add(Box.createVerticalStrut(4));
        titleWords.add(Theme.label("카드 두 장을 뒤집어 Java 개념의 짝을 찾아보세요.", 12, Theme.MUTED));
        title.add(titleWords, BorderLayout.CENTER);
        JPanel difficultyPicker = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        difficultyPicker.setOpaque(false);
        JButton easyButton = difficultyButton("쉬움 · 2×2", !challenge);
        JButton challengeButton = difficultyButton("도전 · 4×4", challenge);
        easyButton.addActionListener(event -> {
            if (challenge) frame.showGame(mission, false);
        });
        challengeButton.addActionListener(event -> {
            if (!challenge) frame.showGame(mission, true);
        });
        difficultyPicker.add(easyButton);
        difficultyPicker.add(challengeButton);
        title.add(difficultyPicker, BorderLayout.NORTH);
        JPanel titleFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleFooter.setOpaque(false);
        titleFooter.add(progress);
        title.add(titleFooter, BorderLayout.SOUTH);
        add(title);
        add(Box.createVerticalStrut(12));

        int columns = deck.size() == 16 ? 4 : 2;
        int rows = (deck.size() + columns - 1) / columns;
        JPanel grid = new JPanel(new GridLayout(rows, columns, 12, 12));
        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(900, challenge ? 390 : 340));
        grid.setMinimumSize(new Dimension(720, challenge ? 360 : 320));
        grid.setMaximumSize(new Dimension(1100, challenge ? 430 : 370));
        for (Card card : deck) {
            MemoryCardButton button = new MemoryCardButton();
            button.setText("?");
            button.setUI(new BasicButtonUI());
            button.setFont(new Font("Malgun Gothic", Font.BOLD, 29));
            button.setForeground(Color.WHITE);
            button.setBackground(Theme.PURPLE);
            button.setOpaque(true);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.PURPLE_DARK, 2),
                    BorderFactory.createEmptyBorder(12, 10, 12, 10)));
            button.setFocusPainted(false);
            button.setToolTipText("뒤집어서 카드 내용을 확인하세요.");
            button.addActionListener(event -> reveal(card, button));
            buttons.add(button);
            grid.add(button);
        }
        JPanel center = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);
        center.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.setMaximumSize(new Dimension(Integer.MAX_VALUE, challenge ? 410 : 360));
        center.add(grid);
        add(center);
        add(Box.createVerticalStrut(12));
        feedback.setAlignmentX(LEFT_ALIGNMENT);
        add(feedback);
        add(Box.createVerticalStrut(10));
        JPanel hint = panelCard();
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        hint.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        hint.add(Theme.label("🐉 기룡이의 힌트  " + mission.hint(), 14, Theme.PURPLE_DARK));
        add(hint);
    }

    private JButton difficultyButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setBackground(selected ? new Color(241, 239, 255) : Color.WHITE);
        button.setForeground(selected ? Theme.PURPLE_DARK : Theme.MUTED);
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 10));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selected ? new Color(202, 194, 245) : Theme.LINE),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)));
        button.setFocusPainted(false);
        return button;
    }

    private void reveal(Card card, JButton button) {
        if (locked || !button.isEnabled() || card == firstCard) {
            return;
        }
        if (firstCard == null) {
            firstCard = card;
            firstButton = button;
            flipCard(card, button, null);
            return;
        }
        locked = true;
        flipCard(card, button, () -> resolvePair(card, button));
    }

    private void resolvePair(Card second, JButton secondButton) {
        if (firstCard.key.equals(second.key) && firstCard.isCode != second.isCode) {
            firstButton.setBackground(new Color(44, 166, 107));
            secondButton.setBackground(new Color(44, 166, 107));
            firstButton.setEnabled(false);
            secondButton.setEnabled(false);
            matched++;
            progress.setText("찾은 짝 " + matched + " / " + (deck.size() / 2));
            feedback.setText("짝을 찾았어요! 다음 카드를 뒤집어 보세요.");
            feedback.setForeground(Theme.GREEN);
            locked = false;
            clearFirst();
            if (matched == deck.size() / 2) {
                completeWithChallenge(matched * 500, challenge ? 2 : 1, challenge);
            }
        } else {
            feedback.setText("아직 짝이 아니에요. 카드를 기억해 두세요!");
            feedback.setForeground(Theme.RED);
            mismatchTimer = new Timer(850, event -> {
                int[] pending = {2};
                Runnable oneHidden = () -> {
                    pending[0]--;
                    if (pending[0] == 0) {
                        clearFirst();
                        locked = false;
                    }
                };
                flipCard(firstCard, firstButton, oneHidden,
                        () -> hideFace(firstCard, firstButton));
                flipCard(second, secondButton, oneHidden,
                        () -> hideFace(second, secondButton));
                mismatchTimer.stop();
            });
            mismatchTimer.setRepeats(false);
            mismatchTimer.start();
        }
    }

    private void flipCard(Card card, JButton button, Runnable finished) {
        flipCard(card, button, finished, () -> showFace(card, button));
    }

    private void flipCard(Card card, JButton button, Runnable finished, Runnable atHalfway) {
        if (button instanceof MemoryCardButton memoryCard) {
            memoryCard.flip(atHalfway, finished);
        } else {
            atHalfway.run();
            if (finished != null) finished.run();
        }
    }

    private void showFace(Card card, JButton button) {
        String prefix = card.isCode ? "<b>코드</b><br>" : "<b>뜻</b><br>";
        button.setText("<html><center>" + prefix + escape(card.text) + "</center></html>");
        button.setFont(new Font(card.isCode ? Font.MONOSPACED : "Malgun Gothic", Font.PLAIN, 14));
        button.setForeground(Theme.INK);
        button.setBackground(Color.WHITE);
        button.setToolTipText(card.text);
    }

    private void hideFace(Card card, JButton button) {
        if (card == null || button == null || !button.isEnabled()) {
            return;
        }
        button.setText("?");
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 29));
        button.setForeground(Color.WHITE);
        button.setBackground(Theme.PURPLE);
        button.setToolTipText("뒤집어서 카드 내용을 확인하세요.");
    }

    private void clearFirst() {
        firstCard = null;
        firstButton = null;
    }

    @Override
    void stopGame() {
        if (mismatchTimer != null) {
            mismatchTimer.stop();
        }
        for (JButton button : buttons) {
            if (button instanceof MemoryCardButton memoryCard) memoryCard.stopFlip();
        }
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final class MemoryCardButton extends JButton {
        private double scaleX = 1d;
        private Timer flipTimer;

        void flip(Runnable swapFace, Runnable finished) {
            stopFlip();
            final long startedAt = System.currentTimeMillis();
            final boolean[] swapped = {false};
            flipTimer = new Timer(16, event -> {
                double progress = Math.min(1d, (System.currentTimeMillis() - startedAt) / 300d);
                scaleX = Math.max(.06d, Math.abs(1d - 2d * progress));
                if (!swapped[0] && progress >= .5d) {
                    swapped[0] = true;
                    swapFace.run();
                }
                repaint();
                if (progress >= 1d) {
                    scaleX = 1d;
                    flipTimer.stop();
                    repaint();
                    if (finished != null) finished.run();
                }
            });
            flipTimer.start();
        }

        void stopFlip() {
            if (flipTimer != null) {
                flipTimer.stop();
                flipTimer = null;
                scaleX = 1d;
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(getWidth() / 2d, 0);
            g.scale(scaleX, 1d);
            g.translate(-getWidth() / 2d, 0);
            super.paintComponent(g);
            g.dispose();
        }
    }

    private record Card(String key, String text, boolean isCode) { }
}
