package kr.ac.kyonggi.javaquest;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

/** Web-demo-inspired side-scrolling adventure map for the Swing app. */
final class JourneyMapPanel extends JPanel {
    private static final int WORLD_WIDTH = 1840;
    private static final int WORLD_HEIGHT = 520;
    private static final boolean BETA_PREVIEW_MODE = true;
    private static final int[] NODE_X = {248, 680, 1120, 1570};
    private static final int[] NODE_Y = {355, 205, 340, 205};
    private static final String[] WORLD_TITLES = {
        "배열과 반복으로 데이터 다루기",
        "클래스·객체·메소드와 검색",
        "여러 클래스의 역할 분담과 객체 간 연관관계",
        "상속·다형성·인터페이스"
    };
    private static final String[] TITLES = {"개념 카드", "오류 수정", "정답 수집", "코딩 보스"};
    private static final String[] SUBTITLES = {"용어 짝 맞추기", "코드 고치기", "기룡이를 좌우로 이동", "조건을 만족하는 코드"};
    private static final String[] ICONS = {"1", "2", "3", "4"};
    private static final PlanetTheme[] PLANETS = {
        new PlanetTheme("지구", PlanetKind.EARTH, new Color(244, 250, 255), new Color(232, 246, 255),
                new Color(215, 230, 249), new Color(155, 220, 151), new Color(98, 185, 128),
                new Color(115, 210, 225), new Color(188, 151, 103), new Color(255, 231, 165)),
        new PlanetTheme("달", PlanetKind.MOON, new Color(13, 18, 35), new Color(34, 41, 63),
                new Color(79, 87, 109), new Color(91, 99, 119), new Color(43, 49, 69),
                null, new Color(163, 146, 231), new Color(78, 143, 202)),
        new PlanetTheme("토성", PlanetKind.SATURN, new Color(15, 18, 39), new Color(48, 32, 62),
                new Color(103, 75, 112), new Color(88, 66, 91), new Color(31, 28, 47),
                null, new Color(212, 158, 91), new Color(226, 180, 115)),
        new PlanetTheme("태양", PlanetKind.SUN, new Color(10, 10, 24), new Color(48, 15, 34),
                new Color(111, 38, 48), new Color(93, 30, 40), new Color(30, 13, 28),
                null, new Color(255, 116, 55), new Color(255, 174, 75))
    };

    private final LearningService service;
    private final BiConsumer<Integer, Integer> openStage;
    private final IntConsumer worldChanged;
    private final JScrollPane mapScroll;
    private final MapWorld world;
    private final JLabel worldProgress = new JLabel();
    private final JLabel worldTitle = Theme.title("");
    private final JLabel worldSubtitle = Theme.label("", 12, Theme.MUTED);
    private final List<JButton> worldButtons = new ArrayList<>();
    private javax.swing.JProgressBar worldProgressBar;
    private int selectedWorld;
    private int selectedStage = 1;
    private Timer scrollTimer;

    JourneyMapPanel(LearningService service, int initialWorld,
                    BiConsumer<Integer, Integer> openStage, IntConsumer worldChanged) {
        this.service = service;
        this.openStage = openStage;
        this.worldChanged = worldChanged;
        selectedWorld = Math.max(1, Math.min(PLANETS.length, initialWorld));
        setLayout(new BorderLayout(0, 0));
        setFocusable(true);
        setBackground(new Color(246, 249, 255));
        setBorder(BorderFactory.createEmptyBorder());
        add(buildHeading(), BorderLayout.NORTH);

        world = new MapWorld();
        mapScroll = new JScrollPane(world, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mapScroll.setBorder(BorderFactory.createEmptyBorder());
        mapScroll.getViewport().setBackground(new Color(238, 247, 255));
        mapScroll.getHorizontalScrollBar().setUnitIncrement(100);
        add(mapScroll, BorderLayout.CENTER);
        installKeyboardNavigation();
        updateWorldHeader();
        updateProgress();
        updateWorldButtons();
    }

    private JPanel buildHeading() {
        JPanel heading = new JPanel(new BorderLayout(20, 0));
        heading.setOpaque(false);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 38, 15, 38));
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new javax.swing.BoxLayout(titleBlock, javax.swing.BoxLayout.Y_AXIS));
        worldSubtitle.setFont(new Font("Malgun Gothic", Font.BOLD, 11));
        worldSubtitle.setForeground(new Color(106, 119, 146));
        titleBlock.add(worldSubtitle);
        titleBlock.add(javax.swing.Box.createVerticalStrut(4));
        worldTitle.setFont(worldTitle.getFont().deriveFont(Font.BOLD, 24f));
        titleBlock.add(worldTitle);
        titleBlock.add(javax.swing.Box.createVerticalStrut(9));
        JPanel selector = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        selector.setOpaque(false);
        for (int index = 0; index < PLANETS.length; index++) {
            final int worldNumber = index + 1;
            JButton button = new JButton("%02d  %s".formatted(worldNumber, PLANETS[index].name));
            button.setName("world-" + worldNumber);
            button.setUI(new BasicButtonUI());
            button.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setPreferredSize(new Dimension(98, 32));
            button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            button.addActionListener(event -> selectWorld(worldNumber));
            worldButtons.add(button);
            selector.add(button);
        }
        titleBlock.add(selector);
        heading.add(titleBlock, BorderLayout.WEST);

        JPanel progressCard = new JPanel();
        progressCard.setLayout(new javax.swing.BoxLayout(progressCard, javax.swing.BoxLayout.Y_AXIS));
        progressCard.setBackground(new Color(249, 251, 255));
        progressCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 243)),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        worldProgress.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
        worldProgress.setForeground(new Color(72, 87, 116));
        worldProgress.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressCard.add(worldProgress);
        progressCard.add(javax.swing.Box.createVerticalStrut(8));
        worldProgressBar = new javax.swing.JProgressBar(0, 4);
        worldProgressBar.setName("world-progress-bar");
        worldProgressBar.setPreferredSize(new Dimension(205, 8));
        worldProgressBar.setMaximumSize(new Dimension(205, 8));
        worldProgressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        worldProgressBar.setBorderPainted(false);
        worldProgressBar.setForeground(new Color(83, 190, 153));
        worldProgressBar.setBackground(new Color(231, 235, 243));
        progressCard.add(worldProgressBar);
        heading.add(progressCard, BorderLayout.EAST);
        return heading;
    }

    private void installKeyboardNavigation() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "nextStage");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "nextStage");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "previousStage");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "previousStage");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "openStage");
        getActionMap().put("nextStage", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { moveSelection(1); }
        });
        getActionMap().put("previousStage", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { moveSelection(-1); }
        });
        getActionMap().put("openStage", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) {
                if (isStageUnlocked(selectedWorld, selectedStage)) {
                    openStage.accept(selectedWorld, selectedStage);
                }
            }
        });
    }

    private void selectWorld(int worldNumber) {
        selectWorld(worldNumber, 1);
    }

    private void selectWorld(int worldNumber, int stageNumber) {
        int targetWorld = Math.max(1, Math.min(PLANETS.length, worldNumber));
        int targetStage = Math.max(1, Math.min(4, stageNumber));
        if (!isStageUnlocked(targetWorld, targetStage)) return;
        selectedWorld = targetWorld;
        selectedStage = targetStage;
        world.setPlanet(PLANETS[selectedWorld - 1]);
        world.animateToSelection();
        world.repaint();
        updateWorldHeader();
        updateProgress();
        updateWorldButtons();
        scrollToStage(selectedStage);
        worldChanged.accept(selectedWorld);
    }

    private void updateWorldHeader() {
        PlanetTheme planet = PLANETS[selectedWorld - 1];
        worldTitle.setText(WORLD_TITLES[selectedWorld - 1]);
        worldSubtitle.setText("WORLD %02d   ·   %s".formatted(selectedWorld, planet.name));
    }

    private void updateWorldButtons() {
        for (JButton button : worldButtons) {
            boolean selected = button.getName().equals("world-" + selectedWorld);
            int worldNumber = Integer.parseInt(button.getName().substring("world-".length()));
            button.setEnabled(isWorldUnlocked(worldNumber));
            button.setForeground(selected ? Theme.PURPLE_DARK : new Color(105, 117, 139));
            button.setBackground(selected ? new Color(241, 239, 255) : Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(selected ? new Color(190, 180, 239) : new Color(226, 232, 242)),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        }
    }

    private void selectStage(int number) {
        int targetStage = Math.max(1, Math.min(4, number));
        if (!isStageUnlocked(selectedWorld, targetStage)) return;
        selectedStage = targetStage;
        world.animateToSelection();
        world.repaint();
        scrollToStage(selectedStage);
    }

    private void moveSelection(int direction) {
        int currentIndex = (selectedWorld - 1) * 4 + selectedStage - 1;
        int nextIndex = currentIndex + direction;
        int lastIndex = PLANETS.length * 4 - 1;
        if (nextIndex < 0 || nextIndex > lastIndex) return;

        int targetWorld = nextIndex / 4 + 1;
        int targetStage = nextIndex % 4 + 1;
        if (!isStageUnlocked(targetWorld, targetStage)) return;
        if (targetWorld == selectedWorld) {
            selectStage(targetStage);
        } else {
            selectWorld(targetWorld, targetStage);
        }
    }

    private void scrollToStage(int stageNumber) {
        int target = Math.max(0, NODE_X[stageNumber - 1] - mapScroll.getViewport().getWidth() / 2);
        target = Math.min(target, Math.max(0, WORLD_WIDTH - mapScroll.getViewport().getWidth()));
        animateScroll(target);
    }

    private boolean isWorldUnlocked(int worldNumber) {
        if (BETA_PREVIEW_MODE || worldNumber <= 1) return true;
        if (worldNumber > service.chapters().size()) return false;
        Chapter previousWorld = service.chapters().get(worldNumber - 2);
        for (Mission mission : previousWorld.missions()) {
            if (service.stars(mission) <= 0) return false;
        }
        return true;
    }

    private boolean isStageUnlocked(int worldNumber, int stageNumber) {
        if (BETA_PREVIEW_MODE) return true;
        if (worldNumber < 1 || worldNumber > PLANETS.length || stageNumber < 1 || stageNumber > 4) return false;
        if (!isWorldUnlocked(worldNumber)) return false;
        if (stageNumber == 1) return true;
        Chapter chapter = service.chapters().get(worldNumber - 1);
        for (int previousStage = 1; previousStage < stageNumber; previousStage++) {
            if (service.stars(chapter.missions().get(previousStage - 1)) <= 0) return false;
        }
        return true;
    }

    private void animateScroll(int targetX) {
        if (scrollTimer != null) scrollTimer.stop();
        int startX = mapScroll.getViewport().getViewPosition().x;
        long startedAt = System.currentTimeMillis();
        scrollTimer = new Timer(16, event -> {
            double progress = Math.min(1d, (System.currentTimeMillis() - startedAt) / 360d);
            double eased = 1 - Math.pow(1 - progress, 3);
            int nextX = (int) Math.round(startX + (targetX - startX) * eased);
            mapScroll.getViewport().setViewPosition(new Point(nextX, 0));
            if (progress >= 1d) scrollTimer.stop();
        });
        scrollTimer.start();
    }

    private void updateProgress() {
        int done = service.completedCount(currentChapter());
        worldProgress.setText("진행도       " + done + " / 4");
        worldProgressBar.setValue(done);
    }

    private Chapter currentChapter() {
        return service.chapters().get(selectedWorld - 1);
    }

    private Mission missionForStage(int stageNumber) {
        return currentChapter().missions().get(stageNumber - 1);
    }

    private final class MapWorld extends JPanel {
        private final List<StageNode> nodes = new ArrayList<>();
        private final List<JLabel> stageTags = new ArrayList<>();
        private final List<JLabel> stageNames = new ArrayList<>();
        private final List<JLabel> stageDescriptions = new ArrayList<>();
        private final MascotMarker mascot = new MascotMarker();
        private PlanetTheme planet = PLANETS[selectedWorld - 1];
        private Timer mascotTimer;

        MapWorld() {
            setLayout(null);
            setPreferredSize(new Dimension(WORLD_WIDTH, WORLD_HEIGHT));
            setMinimumSize(new Dimension(WORLD_WIDTH, WORLD_HEIGHT));
            setOpaque(true);
            setFocusable(true);
            add(mascot);
            setMascotPosition(NODE_X[0] + 63, NODE_Y[0] - 116);
            for (int stage = 1; stage <= 4; stage++) {
                StageNode node = new StageNode(stage);
                final int number = stage;
                node.addActionListener(event -> {
                    if (!isStageUnlocked(selectedWorld, number)) return;
                    selectStage(number);
                    openStage.accept(selectedWorld, number);
                });
                nodes.add(node);
                add(node);
                JLabel tag = new JLabel(stage == 4 ? "4단계 · 최종 보스" : "%d단계".formatted(stage), SwingConstants.CENTER);
                tag.setFont(new Font("Malgun Gothic", Font.BOLD, 11));
                tag.setForeground(new Color(116, 128, 151));
                stageTags.add(tag);
                add(tag);
                JLabel title = new JLabel(TITLES[stage - 1], SwingConstants.CENTER);
                title.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
                title.setForeground(new Color(35, 50, 82));
                stageNames.add(title);
                add(title);
                JLabel subtitle = new JLabel(SUBTITLES[stage - 1], SwingConstants.CENTER);
                subtitle.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
                subtitle.setForeground(new Color(95, 109, 135));
                stageDescriptions.add(subtitle);
                add(subtitle);
            }
            setPlanet(planet);
        }

        void setPlanet(PlanetTheme nextPlanet) {
            planet = nextPlanet;
            boolean darkSky = planet.kind != PlanetKind.EARTH;
            for (JLabel label : stageTags) label.setForeground(darkSky ? new Color(255, 204, 147) : new Color(103, 119, 145));
            for (JLabel label : stageNames) label.setForeground(darkSky ? new Color(250, 247, 255) : new Color(35, 50, 82));
            for (JLabel label : stageDescriptions) label.setForeground(darkSky ? new Color(225, 224, 238) : new Color(83, 99, 125));
            repaint();
        }

        void updateSelection() {
            repaint();
        }

        void animateToSelection() {
            updateSelection();
            if (mascotTimer != null) mascotTimer.stop();
            int startX = mascot.getX();
            int startY = mascot.getY();
            int targetX = selectedStage == 4 ? NODE_X[3] - 145 : NODE_X[selectedStage - 1] + 63;
            int targetY = NODE_Y[selectedStage - 1] - 116;
            long startedAt = System.currentTimeMillis();
            mascotTimer = new Timer(16, event -> {
                double progress = Math.min(1d, (System.currentTimeMillis() - startedAt) / 420d);
                double eased = 1 - Math.pow(1 - progress, 3);
                int x = (int) Math.round(startX + (targetX - startX) * eased);
                int y = (int) Math.round(startY + (targetY - startY) * eased);
                setMascotPosition(x, y);
                if (progress >= 1d) mascotTimer.stop();
            });
            mascotTimer.start();
        }

        private void setMascotPosition(int x, int y) {
            mascot.setBounds(x, y, 76, 125);
        }

        @Override
        public void doLayout() {
            for (int index = 0; index < nodes.size(); index++) {
                int x = NODE_X[index];
                int y = NODE_Y[index];
                nodes.get(index).setBounds(x - 44, y - 44, 88, 88);
                getComponent(2 + index * 4).setBounds(x - 98, y + 48, 196, 18);
                getComponent(3 + index * 4).setBounds(x - 98, y + 65, 196, 24);
                getComponent(4 + index * 4).setBounds(x - 108, y + 89, 216, 20);
            }
            if (mascotTimer == null || !mascotTimer.isRunning()) {
                int mascotX = selectedStage == 4 ? NODE_X[3] - 145 : NODE_X[selectedStage - 1] + 63;
                int mascotY = NODE_Y[selectedStage - 1] - 116;
                setMascotPosition(mascotX, mascotY);
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new java.awt.GradientPaint(0, 0, planet.skyTop, 0, WORLD_HEIGHT, planet.skyBottom));
            g.fillRect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            paintHorizon(g);
            paintTrail(g);
            paintLandscape(g);
            if (planet.kind == PlanetKind.EARTH) paintEarthAdventureDetails(g);
            paintStageLabelBackdrops(g);
            paintControlsHud(g);
            g.dispose();
        }

        private void paintStageLabelBackdrops(Graphics2D g) {
            boolean darkSky = planet.kind != PlanetKind.EARTH;
            for (int index = 0; index < NODE_X.length; index++) {
                int x = NODE_X[index] - 102;
                int y = NODE_Y[index] + 47;
                g.setColor(darkSky ? new Color(13, 18, 36, 166) : new Color(255, 255, 255, 190));
                g.fillRoundRect(x, y, 204, 66, 17, 17);
                g.setColor(darkSky ? new Color(231, 224, 248, 56) : new Color(255, 255, 255, 220));
                g.setStroke(new BasicStroke(1));
                g.drawRoundRect(x, y, 204, 66, 17, 17);
            }
        }

        private void paintControlsHud(Graphics2D g) {
            String controls = BETA_PREVIEW_MODE
                    ? "베타   ·   ←  → 이동     ENTER 입장"
                    : "←  → 이동     ENTER 입장";
            int width = BETA_PREVIEW_MODE ? 356 : 296;
            int x = (WORLD_WIDTH - width) / 2;
            int y = WORLD_HEIGHT - 46;
            g.setColor(new Color(13, 19, 38, 214));
            g.fillRoundRect(x, y, width, 32, 16, 16);
            g.setColor(new Color(240, 235, 249));
            g.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
            int textWidth = g.getFontMetrics().stringWidth(controls);
            g.drawString(controls, x + (width - textWidth) / 2, y + 21);
        }

        private void paintHorizon(Graphics2D g) {
            switch (planet.kind) {
                case EARTH -> {
                    paintOrb(g, 1070, 35, 102, planet.orb, new Color(255, 231, 165, 62));
                    drawMountain(g, 80, 120, 410, 300, planet.mountain);
                    drawMountain(g, 670, 105, 510, 320, blend(planet.mountain, Color.WHITE, .14f));
                    drawMountain(g, 1330, 126, 400, 280, blend(planet.mountain, Color.WHITE, .36f));
                    paintClouds(g, new Color(255, 255, 255, 165));
                }
                case MOON -> {
                    paintStars(g);
                    paintEarthInSky(g, 1125, 42, 88);
                    paintMoonHorizon(g);
                }
                case SATURN -> {
                    paintStars(g);
                    paintSaturnHorizon(g);
                }
                case SUN -> {
                    paintSolarHorizon(g);
                }
            }
        }

        private void paintMoonHorizon(Graphics2D g) {
            GeneralPath farPlain = new GeneralPath();
            farPlain.moveTo(0, 320);
            farPlain.curveTo(270, 312, 470, 322, 720, 316);
            farPlain.curveTo(1010, 308, 1220, 320, 1480, 313);
            farPlain.curveTo(1640, 309, 1750, 316, 1840, 310);
            farPlain.lineTo(1840, 382);
            farPlain.lineTo(0, 382);
            farPlain.closePath();
            g.setColor(new Color(83, 91, 112, 145));
            g.fill(farPlain);

            GeneralPath horizonLine = new GeneralPath();
            horizonLine.moveTo(0, 320);
            horizonLine.curveTo(270, 312, 470, 322, 720, 316);
            horizonLine.curveTo(1010, 308, 1220, 320, 1480, 313);
            horizonLine.curveTo(1640, 309, 1750, 316, 1840, 310);
            g.setColor(new Color(198, 207, 225, 55));
            g.setStroke(new BasicStroke(2));
            g.draw(horizonLine);
        }

        private void paintSaturnHorizon(Graphics2D g) {
            paintRingSystem(g, false);
            paintSaturnPlanet(g, 860, 80, 260);
            paintRingSystem(g, true);
            g.setColor(new Color(247, 215, 175, 135));
            for (int[] dust : new int[][]{{325, 139, 3}, {515, 266, 2}, {1240, 128, 3},
                {1192, 285, 2}, {405, 318, 2}, {1105, 65, 2}}) {
                g.fillOval(dust[0], dust[1], dust[2], dust[2]);
            }
        }

        private void paintRingSystem(Graphics2D g, boolean frontArc) {
            java.awt.geom.AffineTransform before = g.getTransform();
            int centerX = 990;
            int centerY = 210;
            g.rotate(-.115, centerX, centerY);
            if (!frontArc) {
                g.setColor(new Color(79, 59, 98, 115));
                g.setStroke(new BasicStroke(56, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(140, 40, 1700, 340);
                g.setColor(new Color(157, 111, 94, 155));
                g.setStroke(new BasicStroke(30, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(140, 40, 1700, 340);
                g.setColor(new Color(246, 207, 154, 220));
                g.setStroke(new BasicStroke(5));
                g.drawOval(140, 40, 1700, 340);
                g.setColor(new Color(103, 80, 120, 170));
                g.setStroke(new BasicStroke(14));
                g.drawOval(218, 69, 1544, 282);
                g.setColor(new Color(222, 181, 135, 125));
                g.setStroke(new BasicStroke(4));
                g.drawOval(270, 96, 1440, 228);
            } else {
                g.setColor(new Color(232, 180, 126, 225));
                g.setStroke(new BasicStroke(23, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawArc(140, 40, 1700, 340, 180, 180);
                g.setColor(new Color(255, 228, 184, 240));
                g.setStroke(new BasicStroke(4));
                g.drawArc(140, 40, 1700, 340, 180, 180);
            }
            g.setTransform(before);
        }

        private void paintSaturnPlanet(Graphics2D g, int x, int y, int size) {
            java.awt.geom.Ellipse2D.Double disk = new java.awt.geom.Ellipse2D.Double(x, y, size, size);
            java.awt.Shape previousClip = g.getClip();
            g.setClip(disk);
            g.setPaint(new java.awt.GradientPaint(x, y, new Color(246, 211, 158),
                    x + size, y + size, new Color(139, 91, 82)));
            g.fill(disk);
            for (int band = 0; band < 9; band++) {
                int bandY = y + 35 + band * 31;
                int alpha = band % 2 == 0 ? 78 : 42;
                g.setColor(new Color(band % 3 == 0 ? 105 : 255,
                        band % 3 == 0 ? 72 : 226, band % 3 == 0 ? 82 : 176, alpha));
                g.fillOval(x - 22, bandY, size + 44, 17 + band % 3 * 5);
            }
            g.setColor(new Color(115, 70, 74, 145));
            g.fillOval(x + 86, y + 142, 67, 24);
            g.setColor(new Color(255, 237, 202, 100));
            g.fillOval(x + 66, y + 41, 95, 19);
            g.setClip(previousClip);
            g.setColor(new Color(255, 231, 185, 120));
            g.setStroke(new BasicStroke(2));
            g.draw(disk);
            g.setColor(new Color(21, 20, 40, 55));
            g.fillArc(x, y, size, size, 255, 175);
        }

        private void paintEarthInSky(Graphics2D g, int x, int y, int size) {
            paintOrb(g, x, y, size, planet.orb, new Color(101, 158, 231, 48));
            g.setColor(new Color(98, 157, 111, 195));
            g.fillOval(x + 16, y + 24, 25, 14);
            g.fillOval(x + 47, y + 45, 20, 13);
            g.fillOval(x + 58, y + 17, 13, 10);
            g.setColor(new Color(243, 249, 255, 205));
            g.fillOval(x + 19, y + 17, 24, 7);
            g.fillOval(x + 46, y + 61, 19, 6);
            g.setColor(new Color(17, 30, 53, 80));
            g.fillArc(x + 1, y + 1, size - 2, size - 2, 250, 175);
        }

        private void paintEarthAdventureDetails(Graphics2D g) {
            paintCamp(g, 66, 390);
            paintTrailSign(g, 470, 366);
            paintTrailSign(g, 1365, 368);
            paintFootbridge(g, 883, 447, 142);
        }

        private void paintCamp(Graphics2D g, int x, int baseY) {
            g.setColor(new Color(66, 119, 81, 48));
            g.fillOval(x - 8, baseY - 7, 82, 17);
            g.setColor(new Color(184, 123, 76));
            g.fillPolygon(new int[]{x, x + 31, x + 63}, new int[]{baseY, baseY - 43, baseY}, 3);
            g.setColor(new Color(245, 218, 165));
            g.fillPolygon(new int[]{x + 31, x + 63, x + 63}, new int[]{baseY - 43, baseY, baseY}, 3);
            g.setColor(new Color(105, 76, 57));
            g.fillRoundRect(x + 25, baseY - 21, 13, 21, 5, 5);
            g.setColor(new Color(255, 220, 139));
            g.fillOval(x + 72, baseY - 15, 12, 12);
            g.setColor(new Color(234, 148, 75));
            g.fillOval(x + 75, baseY - 12, 6, 8);
        }

        private void paintTrailSign(Graphics2D g, int centerX, int baseY) {
            g.setColor(new Color(101, 75, 53, 210));
            g.fillRoundRect(centerX - 4, baseY - 52, 8, 54, 4, 4);
            int boardY = baseY - 47;
            g.setColor(new Color(190, 143, 91));
            g.fillRoundRect(centerX - 25, boardY, 48, 17, 5, 5);
            g.fillPolygon(
                    new int[]{centerX + 16, centerX + 31, centerX + 16},
                    new int[]{boardY, boardY + 8, boardY + 17}, 3);
            g.setColor(new Color(255, 236, 194, 220));
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(centerX - 15, boardY + 8, centerX + 15, boardY + 8);
        }

        private void paintFootbridge(Graphics2D g, int x, int y, int width) {
            g.setColor(new Color(55, 104, 119, 72));
            g.fillRoundRect(x - 8, y + 7, width + 16, 22, 8, 8);
            g.setColor(new Color(105, 76, 55));
            g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 5, y + 10, x + width - 5, y + 10);
            g.drawLine(x + 5, y + 25, x + width - 5, y + 25);
            g.setColor(new Color(184, 133, 83));
            g.fillRoundRect(x, y + 12, width, 11, 5, 5);
            g.setColor(new Color(235, 194, 132));
            for (int plankX = x + 9; plankX < x + width - 4; plankX += 17) {
                g.drawLine(plankX, y + 13, plankX, y + 21);
            }
        }

        private void paintClouds(Graphics2D g, Color color) {
            g.setColor(color);
            for (int x : new int[]{270, 825, 1430}) {
                g.fillOval(x, 94, 82, 25);
                g.fillOval(x + 28, 78, 72, 41);
                g.fillOval(x + 63, 96, 70, 23);
            }
        }

        private void paintStars(Graphics2D g) {
            g.setColor(new Color(255, 255, 255, 205));
            for (int[] star : new int[][]{
                {150, 58, 3}, {410, 106, 2}, {600, 48, 3}, {870, 72, 2}, {1375, 57, 3},
                {1510, 112, 2}, {1660, 65, 3}, {300, 162, 2}, {940, 145, 2}, {1285, 178, 2}
            }) {
                g.fillOval(star[0], star[1], star[2] * 2, star[2] * 2);
            }
        }

        private void paintOrb(Graphics2D g, int x, int y, int size, Color face, Color halo) {
            g.setColor(halo);
            g.fillOval(x - 24, y - 24, size + 48, size + 48);
            g.setColor(face);
            g.fillOval(x, y, size, size);
        }

        private void drawSaturn(Graphics2D g) {
            int centerX = 1135;
            int centerY = 97;
            java.awt.geom.AffineTransform before = g.getTransform();
            g.rotate(-.16, centerX, centerY);
            g.setColor(new Color(195, 145, 82, 150));
            g.setStroke(new BasicStroke(19));
            g.drawOval(centerX - 92, centerY - 28, 184, 74);
            g.setColor(new Color(255, 227, 170, 230));
            g.setStroke(new BasicStroke(7));
            g.drawOval(centerX - 92, centerY - 28, 184, 74);
            g.setTransform(before);
            paintOrb(g, centerX - 42, centerY - 42, 84, planet.orb, new Color(255, 223, 160, 54));
            g.setColor(new Color(188, 138, 84, 100));
            g.fillOval(centerX - 20, centerY - 16, 17, 11);
            g.fillOval(centerX + 8, centerY + 7, 12, 8);
        }

        private void paintSolarHorizon(Graphics2D g) {
            int centerX = 1000;
            int centerY = 200;
            float radius = 525f;
            g.setPaint(new java.awt.RadialGradientPaint(centerX, centerY, radius,
                    new float[]{0f, .38f, .76f, 1f},
                    new Color[]{new Color(255, 77, 29, 75), new Color(238, 44, 38, 42),
                        new Color(144, 24, 55, 20), new Color(90, 12, 40, 0)}));
            g.fillOval(centerX - (int) radius, centerY - (int) radius,
                    (int) radius * 2, (int) radius * 2);
            paintSolarFlares(g, centerX, centerY, 202);
            paintSolarDisk(g, centerX - 200, centerY - 200, 400);
            paintEmbers(g);
        }

        private void paintSolarFlares(Graphics2D g, int centerX, int centerY, int innerRadius) {
            for (int angle = 0; angle < 360; angle += 12) {
                double radians = Math.toRadians(angle);
                int outerRadius = innerRadius + 45 + (angle * 17 % 88);
                int x1 = centerX + (int) (innerRadius * Math.cos(radians));
                int y1 = centerY + (int) (innerRadius * Math.sin(radians));
                int x2 = centerX + (int) (outerRadius * Math.cos(radians));
                int y2 = centerY + (int) (outerRadius * Math.sin(radians));
                g.setColor(angle % 24 == 0 ? new Color(255, 103, 49, 116)
                        : new Color(255, 177, 75, 70));
                g.setStroke(new BasicStroke(angle % 24 == 0 ? 5 : 2,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(x1, y1, x2, y2);
            }
        }

        private void paintSolarDisk(Graphics2D g, int x, int y, int size) {
            java.awt.geom.Ellipse2D.Double disk = new java.awt.geom.Ellipse2D.Double(x, y, size, size);
            java.awt.Shape previousClip = g.getClip();
            g.setClip(disk);
            g.setPaint(new java.awt.RadialGradientPaint(x + size * .39f, y + size * .34f, size * .82f,
                    new float[]{0f, .42f, .78f, 1f},
                    new Color[]{new Color(255, 239, 157), new Color(255, 173, 67),
                        new Color(245, 83, 41), new Color(172, 34, 49)}));
            g.fill(disk);
            for (int swirl = 0; swirl < 7; swirl++) {
                int startX = x + 18 + swirl * 23;
                int startY = y + 45 + swirl * 43;
                GeneralPath plasmaFlow = new GeneralPath();
                plasmaFlow.moveTo(startX, startY);
                plasmaFlow.curveTo(startX + 100, startY - 50, startX + 133, startY + 45, startX + 202, startY + 12);
                plasmaFlow.curveTo(startX + 257, startY - 15, startX + 299, startY + 46, x + size + 25, startY - 8);
                g.setColor(swirl % 2 == 0 ? new Color(255, 234, 141, 44)
                        : new Color(149, 30, 44, 46));
                g.setStroke(new BasicStroke(9 + swirl % 3 * 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(plasmaFlow);
            }
            g.setColor(new Color(128, 28, 43, 125));
            g.fillOval(x + 90, y + 127, 57, 30);
            g.fillOval(x + 274, y + 252, 39, 22);
            g.setColor(new Color(255, 244, 174, 155));
            g.fillOval(x + 124, y + 78, 78, 17);
            g.fillOval(x + 241, y + 180, 93, 13);
            g.setClip(previousClip);
            g.setColor(new Color(255, 187, 91, 225));
            g.setStroke(new BasicStroke(4));
            g.draw(disk);
        }

        private void paintEmbers(Graphics2D g) {
            for (int[] ember : new int[][]{
                {115, 180, 4}, {330, 78, 3}, {520, 261, 3}, {1270, 96, 4}, {1188, 290, 3},
                {410, 315, 3}, {82, 320, 2}, {1040, 51, 3}, {1232, 218, 2}
            }) {
                g.setColor(new Color(255, 147, 73, 196));
                g.fillOval(ember[0], ember[1], ember[2], ember[2]);
            }
        }

        private void drawMountain(Graphics2D g, int x, int y, int width, int height, Color color) {
            GeneralPath mountain = new GeneralPath();
            mountain.moveTo(x, y + height);
            mountain.lineTo(x + width * .1, y + height * .48);
            mountain.lineTo(x + width * .27, y + height * .67);
            mountain.lineTo(x + width * .49, y);
            mountain.lineTo(x + width * .67, y + height * .58);
            mountain.lineTo(x + width * .79, y + height * .4);
            mountain.lineTo(x + width, y + height);
            mountain.closePath();
            g.setColor(color);
            g.fill(mountain);
        }

        private void paintTrail(Graphics2D g) {
            GeneralPath path = new GeneralPath();
            path.moveTo(NODE_X[0], NODE_Y[0]);
            path.curveTo(400, 350, 450, 215, NODE_X[1], NODE_Y[1]);
            path.curveTo(860, 190, 940, 210, NODE_X[2], NODE_Y[2]);
            path.curveTo(1320, 395, 1395, 205, NODE_X[3], NODE_Y[3]);
            g.setStroke(new BasicStroke(24, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{3, 2}, 0));
            g.setColor(withAlpha(planet.path, 145));
            g.draw(path);
            g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{2, 3}, 0));
            Color trailHighlight = planet.kind == PlanetKind.MOON ? new Color(235, 231, 255, 220)
                    : planet.kind == PlanetKind.SATURN ? new Color(255, 228, 177, 225)
                    : planet.kind == PlanetKind.SUN ? new Color(255, 218, 157, 235)
                    : new Color(255, 255, 255, 225);
            g.setColor(trailHighlight);
            g.draw(path);
        }

        private void paintLandscape(Graphics2D g) {
            if (planet.kind == PlanetKind.MOON) {
                paintMoonSurface(g);
                return;
            }
            if (planet.kind == PlanetKind.SATURN) {
                paintSaturnCloudDeck(g);
                return;
            }
            if (planet.kind == PlanetKind.SUN) {
                paintSolarSurface(g);
                return;
            }
            GeneralPath hill = new GeneralPath();
            hill.moveTo(0, 366);
            hill.curveTo(340, 275, 480, 441, 825, 366);
            hill.curveTo(1120, 295, 1380, 425, 1840, 335);
            hill.lineTo(1840, WORLD_HEIGHT);
            hill.lineTo(0, WORLD_HEIGHT);
            hill.closePath();
            g.setPaint(new java.awt.GradientPaint(0, 360, planet.groundTop, 0, WORLD_HEIGHT, planet.groundBottom));
            g.fill(hill);
            g.setColor(planet.kind == PlanetKind.MOON
                    ? new Color(255, 255, 255, 35) : new Color(255, 255, 255, 48));
            g.setStroke(new BasicStroke(4));
            g.drawArc(30, 360, 1780, 180, 0, 180);
            if (planet.river != null) {
                g.setColor(withAlpha(planet.river, 205));
                GeneralPath river = new GeneralPath();
                river.moveTo(0, 465);
                river.curveTo(320, 430, 470, 502, 770, 463);
                river.curveTo(1110, 420, 1330, 494, 1840, 450);
                river.lineTo(1840, 520);
                river.lineTo(0, 520);
                river.closePath();
                g.fill(river);
            }
        }

        private void paintMoonSurface(Graphics2D g) {
            GeneralPath plain = new GeneralPath();
            plain.moveTo(0, 350);
            plain.curveTo(270, 340, 470, 357, 720, 349);
            plain.curveTo(1010, 339, 1230, 355, 1480, 347);
            plain.curveTo(1630, 342, 1750, 351, 1840, 343);
            plain.lineTo(1840, WORLD_HEIGHT);
            plain.lineTo(0, WORLD_HEIGHT);
            plain.closePath();
            g.setPaint(new java.awt.GradientPaint(0, 348, planet.groundTop, 0, WORLD_HEIGHT, planet.groundBottom));
            g.fill(plain);

            g.setColor(new Color(200, 207, 224, 38));
            g.setStroke(new BasicStroke(2));
            g.drawLine(0, 363, WORLD_WIDTH, 353);
            paintMoonCrater(g, 100, 438, 112, 27);
            paintMoonCrater(g, 490, 405, 78, 19);
            paintMoonCrater(g, 900, 429, 128, 31);
            paintMoonCrater(g, 1375, 409, 91, 22);
            paintMoonCrater(g, 1740, 458, 142, 34);
            paintMoonRock(g, 397, 378, 25, 17);
            paintMoonRock(g, 775, 368, 20, 14);
            paintMoonRock(g, 1260, 379, 29, 18);
            paintMoonRock(g, 1605, 370, 23, 15);
        }

        private void paintMoonCrater(Graphics2D g, int centerX, int centerY, int width, int height) {
            int x = centerX - width / 2;
            int y = centerY - height / 2;
            g.setColor(new Color(29, 35, 52, 100));
            g.fillOval(x, y + 4, width, height);
            g.setColor(new Color(205, 212, 229, 70));
            g.setStroke(new BasicStroke(3));
            g.drawArc(x, y, width, height, 15, 165);
            g.setColor(new Color(47, 54, 73, 190));
            g.fillOval(x + 6, y + 5, width - 12, Math.max(5, height - 9));
            g.setColor(new Color(185, 193, 213, 45));
            g.setStroke(new BasicStroke(2));
            g.drawArc(x + 9, y + 7, width - 18, Math.max(4, height - 13), 10, 150);
        }

        private void paintMoonRock(Graphics2D g, int centerX, int baseY, int width, int height) {
            g.setColor(new Color(25, 31, 48, 75));
            g.fillOval(centerX - width / 2 - 4, baseY - 3, width + 8, 8);
            GeneralPath rock = new GeneralPath();
            rock.moveTo(centerX - width / 2, baseY);
            rock.lineTo(centerX - width / 3, baseY - height * .65f);
            rock.lineTo(centerX + width * .1f, baseY - height);
            rock.lineTo(centerX + width / 2, baseY - height * .58f);
            rock.lineTo(centerX + width / 2, baseY);
            rock.closePath();
            g.setColor(new Color(93, 101, 122));
            g.fill(rock);
            g.setColor(new Color(200, 207, 222, 85));
            g.setStroke(new BasicStroke(2));
            g.drawLine(centerX - width / 3, baseY - Math.round(height * .62f),
                    centerX + width / 10, baseY - height + 2);
        }

        private void paintSaturnCloudDeck(Graphics2D g) {
            GeneralPath cloudDeck = new GeneralPath();
            cloudDeck.moveTo(0, 365);
            cloudDeck.curveTo(260, 346, 485, 373, 750, 359);
            cloudDeck.curveTo(1035, 345, 1265, 370, 1515, 356);
            cloudDeck.curveTo(1640, 350, 1750, 363, 1840, 350);
            cloudDeck.lineTo(1840, WORLD_HEIGHT);
            cloudDeck.lineTo(0, WORLD_HEIGHT);
            cloudDeck.closePath();
            g.setPaint(new java.awt.GradientPaint(0, 355, new Color(91, 70, 98),
                    0, WORLD_HEIGHT, new Color(29, 27, 45)));
            g.fill(cloudDeck);

            g.setColor(new Color(236, 181, 129, 52));
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(0, 370, WORLD_WIDTH, 358);
            for (int row = 0; row < 4; row++) {
                int y = 394 + row * 42;
                g.setColor(new Color(218, 166, 142, 24 + row * 3));
                g.setStroke(new BasicStroke(7 - row, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawArc(-120 + row * 120, y, 930, 96, 12, 156);
                g.drawArc(720 + row * 70, y - 18, 1110, 120, 198, 146);
            }
            paintSaturnStorm(g, 310, 452, 118, 38);
            paintSaturnStorm(g, 985, 494, 156, 47);
            paintSaturnStorm(g, 1580, 440, 107, 32);
        }

        private void paintSaturnStorm(Graphics2D g, int centerX, int centerY, int width, int height) {
            g.setColor(new Color(23, 23, 41, 120));
            g.fillOval(centerX - width / 2, centerY - height / 2 + 5, width, height);
            g.setColor(new Color(209, 152, 122, 66));
            g.setStroke(new BasicStroke(3));
            g.drawArc(centerX - width / 2, centerY - height / 2, width, height, 14, 192);
            g.setColor(new Color(113, 77, 103, 112));
            g.fillOval(centerX - width / 3, centerY - height / 3, width * 2 / 3, height * 2 / 3);
        }

        private void paintSolarSurface(Graphics2D g) {
            int[][] ridge = {
                {0, 375}, {95, 367}, {172, 379}, {268, 359}, {352, 376}, {472, 364},
                {558, 381}, {668, 362}, {785, 378}, {896, 358}, {1010, 377}, {1115, 359},
                {1215, 379}, {1320, 365}, {1430, 382}, {1540, 361}, {1660, 378}, {1760, 359}, {1840, 370}
            };
            GeneralPath crust = new GeneralPath();
            crust.moveTo(ridge[0][0], ridge[0][1]);
            for (int point = 1; point < ridge.length; point++) {
                crust.lineTo(ridge[point][0], ridge[point][1]);
            }
            crust.lineTo(1840, WORLD_HEIGHT);
            crust.lineTo(0, WORLD_HEIGHT);
            crust.closePath();
            g.setPaint(new java.awt.GradientPaint(0, 365, new Color(91, 31, 41),
                    0, WORLD_HEIGHT, new Color(23, 12, 27)));
            g.fill(crust);

            GeneralPath crustEdge = new GeneralPath();
            crustEdge.moveTo(ridge[0][0], ridge[0][1]);
            for (int point = 1; point < ridge.length; point++) {
                crustEdge.lineTo(ridge[point][0], ridge[point][1]);
            }
            g.setColor(new Color(255, 116, 61, 190));
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(crustEdge);

            for (int[] crack : new int[][]{
                {158, 396, 34, 22}, {431, 408, 43, 27}, {716, 397, 38, 30},
                {1000, 410, 47, 29}, {1278, 397, 42, 32}, {1587, 411, 46, 30}
            }) {
                GeneralPath fissure = new GeneralPath();
                fissure.moveTo(crack[0], crack[1]);
                fissure.lineTo(crack[0] + crack[2] / 2, crack[1] + crack[3] / 2);
                fissure.lineTo(crack[0] + crack[2] / 3, crack[1] + crack[3]);
                fissure.moveTo(crack[0] + crack[2] / 2, crack[1] + crack[3] / 2);
                fissure.lineTo(crack[0] + crack[2], crack[1] + crack[3] / 3);
                g.setColor(new Color(255, 91, 49, 125));
                g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(fissure);
                g.setColor(new Color(255, 202, 103, 220));
                g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(fissure);
            }
            g.setColor(new Color(255, 183, 92, 100));
            for (int x = 56; x < WORLD_WIDTH; x += 190) {
                g.fillOval(x, 386 + (x % 3) * 9, 4, 4);
            }
        }
    }

    private final class StageNode extends JButton {
        private final int number;

        StageNode(int number) {
            this.number = number;
            setToolTipText(TITLES[number - 1] + " · " + SUBTITLES[number - 1]);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent event) { animateHover(true); }
                @Override public void mouseExited(MouseEvent event) { animateHover(false); }
            });
        }

        private float hoverAmount;
        private Timer hoverTimer;

        private void animateHover(boolean hovering) {
            if (hoverTimer != null) hoverTimer.stop();
            float start = hoverAmount;
            float end = hovering ? 1f : 0f;
            long startedAt = System.currentTimeMillis();
            hoverTimer = new Timer(16, event -> {
                double progress = Math.min(1d, (System.currentTimeMillis() - startedAt) / 150d);
                double eased = 1 - Math.pow(1 - progress, 3);
                hoverAmount = (float) (start + (end - start) * eased);
                repaint();
                if (progress >= 1d) hoverTimer.stop();
            });
            hoverTimer.start();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(0, -5 * hoverAmount);
            int stars = number == 1 ? service.stars(missionForStage(1))
                    : number == 3 ? service.stars(missionForStage(3)) : 0;
            boolean locked = !isStageUnlocked(selectedWorld, number);
            boolean demo = number == 3;
            boolean selected = number == selectedStage;
            Color face = locked ? new Color(218, 224, 234) : stars > 0 ? new Color(66, 190, 131)
                    : demo ? new Color(48, 180, 217) : new Color(112, 86, 217);
            Color shade = locked ? new Color(183, 191, 204) : stars > 0 ? new Color(37, 136, 91)
                    : demo ? new Color(22, 130, 167) : new Color(79, 64, 167);
            if (selected) {
                g.setColor(new Color(255, 211, 107, 110));
                g.fillRoundRect(1, 1, 86, 86, 33, 33);
            }
            g.setColor(shade);
            g.fillRoundRect(7, 9, 74, 74, 28, 28);
            g.setColor(face);
            g.fillRoundRect(7, 3, 74, 74, 28, 28);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(5));
            g.drawRoundRect(8, 4, 72, 72, 27, 27);
            if (locked) {
                g.setColor(new Color(153, 163, 178));
                g.setStroke(new BasicStroke(5));
                g.drawRoundRect(33, 31, 23, 19, 7, 7);
                g.drawArc(37, 20, 16, 23, 0, 180);
            } else {
                g.setFont(new Font("Malgun Gothic", Font.BOLD, 29));
                String icon = ICONS[number - 1];
                int width = g.getFontMetrics().stringWidth(icon);
                g.drawString(icon, 44 - width / 2, 52);
            }
            if (stars > 0) {
                g.setColor(new Color(255, 211, 107));
                g.fillRoundRect(56, -1, 36, 20, 11, 11);
                g.setColor(new Color(127, 82, 20));
                g.setFont(new Font("Malgun Gothic", Font.BOLD, 10));
                g.drawString("★".repeat(Math.min(stars, 3)), 61, 13);
            }
            g.dispose();
        }
    }

    private static Color withAlpha(Color color, int alpha) {
        if (color == null) return null;
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private static Color blend(Color first, Color second, float amount) {
        float ratio = Math.max(0f, Math.min(1f, amount));
        return new Color(
                Math.round(first.getRed() + (second.getRed() - first.getRed()) * ratio),
                Math.round(first.getGreen() + (second.getGreen() - first.getGreen()) * ratio),
                Math.round(first.getBlue() + (second.getBlue() - first.getBlue()) * ratio));
    }

    private enum PlanetKind { EARTH, MOON, SATURN, SUN }

    private record PlanetTheme(
            String name,
            PlanetKind kind,
            Color skyTop,
            Color skyBottom,
            Color mountain,
            Color groundTop,
            Color groundBottom,
            Color river,
            Color path,
            Color orb) { }

    private static final class MascotMarker extends JPanel {
        MascotMarker() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            Theme.drawGiryong(g, 0, 0, getWidth(), getHeight());
            g.dispose();
        }
    }
}
