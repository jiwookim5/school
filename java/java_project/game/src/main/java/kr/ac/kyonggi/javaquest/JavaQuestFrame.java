package kr.ac.kyonggi.javaquest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

/** Main window styled and navigated like the browser demo, implemented with Swing. */
public final class JavaQuestFrame extends JFrame {
    private enum Nav { MAP, COLLECTION, CHARACTER }

    private final LearningService service;
    private final JPanel shell = new JPanel(new BorderLayout());
    private final JPanel topBar = new JPanel(new BorderLayout());
    private final JPanel pageHost = new JPanel(new BorderLayout());
    private final JPanel bottomNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    private JLabel pointValue;
    private JLabel worldBreadcrumb;
    private int selectedWorld = 1;
    private Nav selectedNav = Nav.MAP;
    private GamePanelBase activeGame;
    private Timer damageFlashTimer;

    public JavaQuestFrame(LearningService service) {
        super("JAVA QUEST — 기룡이의 자바 월드");
        this.service = service;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 680));
        setSize(1280, 800);
        setLocationByPlatform(true);

        shell.setBackground(Theme.BACKGROUND);
        shell.add(buildTopBar(), BorderLayout.NORTH);
        pageHost.setBackground(Theme.BACKGROUND);
        shell.add(pageHost, BorderLayout.CENTER);
        shell.add(buildBottomNav(), BorderLayout.SOUTH);
        setContentPane(shell);
        showMap();
    }

    private JPanel buildTopBar() {
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(1000, 76));
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 234, 243)),
                BorderFactory.createEmptyBorder(0, 38, 0, 38)));

        JButton brand = new JButton();
        brand.setUI(new BasicButtonUI());
        brand.setOpaque(false);
        brand.setContentAreaFilled(false);
        brand.setBorderPainted(false);
        brand.setFocusPainted(false);
        brand.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        JPanel brandContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 11, 0));
        brandContent.setOpaque(false);
        JLabel mark = new JLabel("JQ", SwingConstants.CENTER);
        mark.setPreferredSize(new Dimension(42, 42));
        mark.setOpaque(true);
        mark.setBackground(Theme.PURPLE);
        mark.setForeground(Color.WHITE);
        mark.setFont(new Font("Malgun Gothic", Font.BOLD, 19));
        mark.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(79, 63, 175), 1),
                BorderFactory.createEmptyBorder(0, 0, 2, 0)));
        JPanel brandWords = new JPanel();
        brandWords.setOpaque(false);
        brandWords.setLayout(new BoxLayout(brandWords, BoxLayout.Y_AXIS));
        JLabel name = Theme.label("JAVA QUEST", 15, Theme.INK);
        name.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        JLabel tagline = Theme.label("GIRYONG WORLD", 8, new Color(151, 161, 179));
        tagline.setFont(new Font("Malgun Gothic", Font.BOLD, 8));
        brandWords.add(name);
        brandWords.add(Box.createVerticalStrut(4));
        brandWords.add(tagline);
        brandContent.add(mark);
        brandContent.add(brandWords);
        brand.add(brandContent);
        brand.addActionListener(event -> showMap());
        topBar.add(brand, BorderLayout.WEST);

        worldBreadcrumb = Theme.label("●  행성 탐험     /     WORLD 01 · 지구", 13, new Color(72, 86, 115));
        worldBreadcrumb.setFont(new Font("Malgun Gothic", Font.BOLD, 13));
        worldBreadcrumb.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(worldBreadcrumb, BorderLayout.CENTER);

        JPanel status = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        status.setOpaque(false);
        status.add(statusPill("♨", Integer.toString(service.user().streakDays()), "연속 학습", new Color(247, 131, 102)));
        status.add(statusPill("★", Integer.toString(growthPoints()), "성장 포인트", new Color(239, 173, 39)));
        JButton profile = new JButton(service.user().nickname().substring(0, 1));
        profile.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        profile.setForeground(Color.WHITE);
        profile.setBackground(new Color(80, 188, 148));
        profile.setBorderPainted(false);
        profile.setFocusPainted(false);
        profile.setPreferredSize(new Dimension(40, 40));
        status.add(profile);
        topBar.add(status, BorderLayout.EAST);
        refreshPointValue();
        return topBar;
    }

    private JPanel statusPill(String icon, String value, String label, Color iconColor) {
        JPanel pill = new JPanel(new BorderLayout(5, 0));
        pill.setBackground(Color.WHITE);
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 234, 243)),
                BorderFactory.createEmptyBorder(4, 9, 4, 9)));
        JLabel mark = Theme.label(icon, 18, iconColor);
        pill.add(mark, BorderLayout.WEST);
        JPanel words = new JPanel();
        words.setOpaque(false);
        words.setLayout(new BoxLayout(words, BoxLayout.Y_AXIS));
        JLabel valueLabel = Theme.label(value, 13, Theme.INK);
        valueLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 13));
        if (label.equals("성장 포인트")) pointValue = valueLabel;
        words.add(valueLabel);
        JLabel caption = Theme.label(label, 9, new Color(135, 146, 166));
        words.add(caption);
        pill.add(words, BorderLayout.CENTER);
        return pill;
    }

    private JPanel buildBottomNav() {
        bottomNav.setBackground(Color.WHITE);
        bottomNav.setPreferredSize(new Dimension(1000, 66));
        bottomNav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 234, 243)));
        bottomNav.add(navButton("⌖   월드맵", Nav.MAP, this::showMap));
        bottomNav.add(navButton("▣   도감", Nav.COLLECTION, this::showCollection));
        bottomNav.add(navButton("♧   기룡이", Nav.CHARACTER, this::showCharacter));
        return bottomNav;
    }

    private JButton navButton(String text, Nav nav, Runnable action) {
        JButton button = new JButton(text);
        button.setName("nav-" + nav.name().toLowerCase());
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
        button.setForeground(nav == selectedNav ? Theme.PURPLE : new Color(140, 150, 170));
        button.setBackground(Color.WHITE);
        button.setOpaque(true);
        button.setUI(new BasicButtonUI());
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(112, 64));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.addActionListener(event -> action.run());
        return button;
    }

    void showHome() {
        showMap();
    }

    void showMap() {
        leaveGame();
        selectedNav = Nav.MAP;
        setChromeVisible(true);
        refreshNav();
        JourneyMapPanel map = new JourneyMapPanel(service, selectedWorld, this::openStage, this::selectWorld);
        displayPanel(map, false);
        SwingUtilities.invokeLater(map::requestFocusInWindow);
    }

    private void selectWorld(int worldNumber) {
        selectedWorld = Math.max(1, Math.min(4, worldNumber));
        updateWorldBreadcrumb();
    }

    private void openStage(int worldNumber, int stageNumber) {
        selectWorld(worldNumber);
        if (stageNumber == 1 || stageNumber == 3 || stageNumber == 4) {
            Chapter world = service.chapters().get(worldNumber - 1);
            showGame(world.missions().get(stageNumber - 1));
            return;
        }
        String title = stageNumber == 2 ? "2단계 · 코드 오류 찾기" : "2단계";
        JOptionPane.showMessageDialog(this,
                "이 스테이지 화면은 팀에서 함께 구현 중이에요.\n현재 시연에서는 개념 카드와 내려오는 정답을 플레이할 수 있어요.",
                title, JOptionPane.INFORMATION_MESSAGE);
    }

    void showCollection() {
        leaveGame();
        selectedNav = Nav.COLLECTION;
        setChromeVisible(true);
        refreshNav();
        JPanel page = contentPage("기룡이 도감", "클리어한 미션에서 얻은 별을 모아 봐요.");
        for (Mission mission : playableMissions()) {
            JPanel item = Theme.card();
            item.setLayout(new BorderLayout(12, 0));
            item.add(Theme.label("STAGE %02d · %s".formatted(mission.number(), mission.type().title()), 15, Theme.INK), BorderLayout.CENTER);
            item.add(Theme.label(starText(service.stars(mission)), 18, Theme.YELLOW), BorderLayout.EAST);
            page.add(item);
            page.add(Box.createVerticalStrut(10));
        }
        displayPanel(page, true);
    }

    void showCharacter() {
        leaveGame();
        selectedNav = Nav.CHARACTER;
        setChromeVisible(true);
        refreshNav();
        JPanel page = contentPage("기룡이 성장", "미션을 완료하고 성장 포인트를 모아 기룡이와 친해져 보세요.");
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(20, 0));
        card.add(new Theme.Mascot(), BorderLayout.WEST);
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.add(Theme.heading(service.user().nickname() + "와 함께 모험 중!") );
        details.add(Box.createVerticalStrut(12));
        details.add(Theme.label("성장 포인트   " + growthPoints() + "점", 16, Theme.PURPLE));
        details.add(Box.createVerticalStrut(6));
        details.add(Theme.label("획득한 별   " + service.totalStars() + "개", 14, Theme.YELLOW));
        card.add(details, BorderLayout.CENTER);
        page.add(card);
        displayPanel(page, true);
    }

    void showChapter(int chapterNumber) {
        showMap();
    }

    void showGame(Mission mission) {
        showGame(mission, false);
    }

    void showGame(Mission mission, boolean challenge) {
        leaveGame();
        selectedWorld = Math.max(1, Math.min(4, mission.chapterNumber()));
        updateWorldBreadcrumb();
        try {
            activeGame = switch (mission.type()) {
                case MATCHING -> new MatchingGamePanel(this, service, mission, challenge);
                case TYPING -> new TypingGamePanel(this, service, mission);
                case STACK -> new CatchGamePanel(this, service, mission, challenge);
                case HAND_CODING -> new HandCodingGamePanel(this, service, mission);
            };
            selectedNav = Nav.MAP;
            refreshNav();
            boolean fullScreenGame = mission.type() == GameType.STACK;
            setChromeVisible(!fullScreenGame);
            displayPanel(activeGame, true);
        } catch (Exception e) {
            System.err.println("게임 로드 에러: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "게임 로드 실패: " + e.getMessage());
            showMap();
        }
    }

    void flashDamage() {
        if (damageFlashTimer != null) damageFlashTimer.stop();
        shell.setBorder(BorderFactory.createLineBorder(new Color(237, 75, 75), 7));
        damageFlashTimer = new Timer(420, event -> {
            shell.setBorder(null);
            damageFlashTimer.stop();
        });
        damageFlashTimer.setRepeats(false);
        damageFlashTimer.start();
    }

    private JPanel contentPage(String title, String subtitle) {
        JPanel page = new JPanel();
        page.setBackground(new Color(247, 249, 252));
        page.setBorder(BorderFactory.createEmptyBorder(35, 48, 35, 48));
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.add(Theme.title(title));
        page.add(Box.createVerticalStrut(5));
        page.add(Theme.label(subtitle, 13, Theme.MUTED));
        page.add(Box.createVerticalStrut(18));
        return page;
    }

    private void displayPanel(JPanel panel, boolean scroll) {
        pageHost.removeAll();
        JPanel animated = new FadeInPanel(panel);
        if (scroll) {
            JScrollPane viewport = new JScrollPane(animated);
            viewport.setBorder(BorderFactory.createEmptyBorder());
            viewport.getVerticalScrollBar().setUnitIncrement(16);
            viewport.getViewport().setBackground(new Color(247, 249, 252));
            pageHost.add(viewport, BorderLayout.CENTER);
        } else {
            pageHost.add(animated, BorderLayout.CENTER);
        }
        pageHost.revalidate();
        pageHost.repaint();
        refreshPointValue();
    }

    private void setChromeVisible(boolean visible) {
        topBar.setVisible(visible);
        bottomNav.setVisible(visible);
        shell.revalidate();
        shell.repaint();
    }

    private void refreshNav() {
        for (Component component : bottomNav.getComponents()) {
            if (component instanceof JButton button) {
                String name = button.getName();
                boolean active = name != null && name.equals("nav-" + selectedNav.name().toLowerCase());
                button.setForeground(active ? Theme.PURPLE : new Color(140, 150, 170));
            }
        }
    }

    private void refreshPointValue() {
        if (pointValue != null) pointValue.setText(Integer.toString(growthPoints()));
    }

    private int growthPoints() {
        return service.totalStars() * 25;
    }

    private List<Mission> playableMissions() {
        Chapter chapter = service.chapters().get(selectedWorld - 1);
        return List.of(chapter.missions().get(0), chapter.missions().get(2));
    }

    private void updateWorldBreadcrumb() {
        if (worldBreadcrumb != null) {
            String[] planets = {"지구", "달", "토성", "태양"};
            worldBreadcrumb.setText("●  행성 탐험     /     WORLD %02d · %s".formatted(selectedWorld, planets[selectedWorld - 1]));
        }
    }

    private String starText(int amount) {
        return "★".repeat(Math.max(0, Math.min(3, amount)))
                + "☆".repeat(3 - Math.max(0, Math.min(3, amount)));
    }

    private void leaveGame() {
        if (activeGame != null) {
            activeGame.stopGame();
            activeGame = null;
        }
    }

    private static final class FadeInPanel extends JPanel {
        private float opacity;

        FadeInPanel(JPanel content) {
            super(new BorderLayout());
            setOpaque(false);
            add(content, BorderLayout.CENTER);
            Timer fade = new Timer(16, null);
            long startedAt = System.currentTimeMillis();
            fade.addActionListener(event -> {
                opacity = Math.min(1f, (System.currentTimeMillis() - startedAt) / 220f);
                repaint();
                if (opacity >= 1f) fade.stop();
            });
            fade.start();
        }

        @Override
        protected void paintChildren(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setComposite(AlphaComposite.SrcOver.derive(opacity));
            super.paintChildren(g);
            g.dispose();
        }
    }
}
