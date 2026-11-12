package teamapi.v1;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HandCodingGameGUI extends JFrame {
    private GameService gameService;
    private String currentGameId;
    private String currentUserId;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JPanel loginPanel;
    private JPanel chapterPanel;
    private JPanel gamePanel;
    private JPanel resultPanel;

    private Question currentQuestion;
    private GameResult currentResult;

    private JTextArea gameProblemArea;
    private JTextArea gameResultArea;
    private JTextArea gameCodeArea;
    private JTextField gameInputField;
    private JButton gameSubmitButton;
    private JLabel gameTimeLabel;
    private JButton gameNextButton;
    private javax.swing.Timer gameTimer;
    private JLabel gameMessageLabel;
    private JProgressBar gameGaugeBar;
    private JLabel gameGaugeLabel;
    private JLabel gameProgressLabel;
    private boolean lastAnswerCorrect;

    public HandCodingGameGUI(GameService gameService) {
        this.gameService = gameService;
        setTitle("☕ JAVA QUEST - HAND CODING");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = createLoginPanel();
        chapterPanel = createChapterPanel();
        gamePanel = createGamePanel();
        resultPanel = createResultPanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(chapterPanel, "CHAPTER");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(resultPanel, "RESULT");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
        setVisible(true);
    }

    // ==================== 로그인 화면 ====================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // 제목
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("☕ JAVA QUEST");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // 입력 영역
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(240, 248, 255));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));

        JLabel userLabel = new JLabel("사용자명:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userLabel.setForeground(Color.BLACK);
        JTextField userField = new JTextField();
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        userField.setPreferredSize(new Dimension(300, 40));
        userField.setText("user123");
        userField.setForeground(Color.BLACK);

        JLabel passLabel = new JLabel("비밀번호:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passLabel.setForeground(Color.BLACK);
        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setPreferredSize(new Dimension(300, 40));
        passField.setText("pass");

        JButton loginButton = new JButton("로그인");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(300, 50));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);

        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(userLabel);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(userField);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(passLabel);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(passField);
        inputPanel.add(Box.createVerticalStrut(40));
        inputPanel.add(loginButton);

        panel.add(inputPanel, BorderLayout.CENTER);

        // 로그인 버튼 액션
        loginButton.addActionListener(e -> {
            String userId = userField.getText();
            if (userId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "사용자명을 입력하세요!");
                return;
            }
            currentUserId = userId;
            cardLayout.show(mainPanel, "CHAPTER");
        });

        return panel;
    }

    // ==================== 챕터 선택 화면 ====================
    private JPanel createChapterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // 제목
        JLabel titleLabel = new JLabel("📚 챕터 선택");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 248, 255));
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // 챕터 버튼들
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setLayout(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        for (int i = 1; i <= 2; i++) {
            JButton chapterButton = new JButton("📖 챕터 " + i);
            chapterButton.setFont(new Font("Arial", Font.BOLD, 18));
            chapterButton.setBackground(new Color(100, 149, 237));
            chapterButton.setForeground(Color.BLACK);
            chapterButton.setFocusPainted(false);
            chapterButton.setPreferredSize(new Dimension(200, 80));

            final int chapterId = i;
            chapterButton.addActionListener(e -> {
                try {
                    startGame(currentUserId, chapterId);

                    // 도움말 표시
                    showGameHelp();

                    updateGameScreen();
                    gameCodeArea.setText("");
                    gameMessageLabel.setText("");
                    gameSubmitButton.setEnabled(true);
                    gameNextButton.setEnabled(false);
                    gameResultArea.setText("");
                    cardLayout.show(mainPanel, "GAME");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(HandCodingGameGUI.this,
                        "게임 시작 오류: " + ex.getMessage());
                }
            });

            buttonPanel.add(chapterButton);
        }

        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    // ==================== 게임 화면 ====================
    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // 상단: 게이지 + 진행도
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 248, 255));
        gameProgressLabel = new JLabel("진행도: ");
        gameProgressLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gameProgressLabel.setForeground(new Color(70, 130, 180));
        statusPanel.add(gameProgressLabel);
        headerPanel.add(statusPanel, BorderLayout.WEST);

        // 중앙: 타이머
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timerPanel.setBackground(new Color(240, 248, 255));
        gameTimeLabel = new JLabel("⏱️ 남은 시간: 10:00");
        gameTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gameTimeLabel.setForeground(new Color(70, 130, 180));
        timerPanel.add(gameTimeLabel);
        headerPanel.add(timerPanel, BorderLayout.CENTER);

        JPanel gaugePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        gaugePanel.setBackground(new Color(240, 248, 255));
        JLabel gaugeTitle = new JLabel("게이지: ");
        gaugeTitle.setFont(new Font("Arial", Font.BOLD, 13));
        gaugeTitle.setForeground(Color.BLACK);
        gameGaugeLabel = new JLabel("50/100");
        gameGaugeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gameGaugeLabel.setForeground(new Color(220, 20, 60));
        gameGaugeBar = new JProgressBar(0, 100);
        gameGaugeBar.setValue(50);
        gameGaugeBar.setPreferredSize(new Dimension(200, 20));
        gameGaugeBar.setStringPainted(true);
        gameGaugeBar.setString("50/100");
        gaugePanel.add(gaugeTitle);
        gaugePanel.add(gameGaugeBar);
        gaugePanel.add(gameGaugeLabel);
        headerPanel.add(gaugePanel, BorderLayout.EAST);

        // 중앙: 좌우 분할 (문제 1/4 | 코드 3/4)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(15, 0, 15, 0);

        // 왼쪽: 문제 설명 (1/4)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(240, 248, 255));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));

        JLabel problemLabel = new JLabel("📌 문제");
        problemLabel.setFont(new Font("Arial", Font.BOLD, 14));
        problemLabel.setForeground(Color.BLACK);
        leftPanel.add(problemLabel, BorderLayout.NORTH);

        gameProblemArea = new JTextArea();
        gameProblemArea.setFont(new Font("Arial", Font.PLAIN, 13));
        gameProblemArea.setForeground(Color.BLACK);
        gameProblemArea.setEditable(false);
        gameProblemArea.setBackground(new Color(255, 255, 255));
        gameProblemArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gameProblemArea.setLineWrap(true);
        gameProblemArea.setWrapStyleWord(true);
        JScrollPane problemScroll = new JScrollPane(gameProblemArea);
        leftPanel.add(problemScroll, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.weightx = 0.25;
        contentPanel.add(leftPanel, gbc);

        // 오른쪽: 코드 입력 (3/4)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(240, 248, 255));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

        JLabel codeLabel = new JLabel("💻 코드 입력");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        codeLabel.setForeground(Color.BLACK);
        rightPanel.add(codeLabel, BorderLayout.NORTH);

        gameCodeArea = new JTextArea();
        gameCodeArea.setFont(new Font("Courier", Font.PLAIN, 13));
        gameCodeArea.setForeground(Color.BLACK);
        gameCodeArea.setBackground(new Color(255, 255, 255));
        gameCodeArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gameCodeArea.setLineWrap(true);
        gameCodeArea.setWrapStyleWord(false);
        gameCodeArea.setTabSize(4);
        JScrollPane codeScroll = new JScrollPane(gameCodeArea);

        // 입력값 필드
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        inputPanel.setBackground(new Color(240, 248, 255));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel inputLabel = new JLabel("📥 입력값: ");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 12));
        inputLabel.setForeground(Color.BLACK);

        gameInputField = new JTextField(30);
        gameInputField.setFont(new Font("Arial", Font.PLAIN, 12));
        gameInputField.setForeground(Color.BLACK);
        gameInputField.setBackground(new Color(255, 255, 255));
        gameInputField.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        inputPanel.add(inputLabel);
        inputPanel.add(gameInputField);

        // 코드 + 입력 영역 결합
        JPanel codeAndInputPanel = new JPanel(new BorderLayout());
        codeAndInputPanel.setBackground(new Color(240, 248, 255));
        codeAndInputPanel.add(codeScroll, BorderLayout.CENTER);
        codeAndInputPanel.add(inputPanel, BorderLayout.SOUTH);

        rightPanel.add(codeAndInputPanel, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        contentPanel.add(rightPanel, gbc);

        // 하단: 버튼 + 메시지
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton compileButton = new JButton("🔨 컴파일");
        compileButton.setFont(new Font("Arial", Font.BOLD, 14));
        compileButton.setBackground(new Color(100, 149, 237));
        compileButton.setForeground(Color.BLACK);
        compileButton.setFocusPainted(false);
        compileButton.setPreferredSize(new Dimension(120, 40));

        gameSubmitButton = new JButton("✅ 정답 확인");
        gameSubmitButton.setFont(new Font("Arial", Font.BOLD, 14));
        gameSubmitButton.setBackground(new Color(50, 205, 50));
        gameSubmitButton.setForeground(Color.BLACK);
        gameSubmitButton.setFocusPainted(false);
        gameSubmitButton.setPreferredSize(new Dimension(140, 40));

        gameNextButton = new JButton("➡️ 다음");
        gameNextButton.setFont(new Font("Arial", Font.BOLD, 14));
        gameNextButton.setBackground(new Color(70, 130, 180));
        gameNextButton.setForeground(Color.BLACK);
        gameNextButton.setFocusPainted(false);
        gameNextButton.setPreferredSize(new Dimension(120, 40));
        gameNextButton.setEnabled(false);

        gameMessageLabel = new JLabel("");
        gameMessageLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gameMessageLabel.setForeground(Color.BLACK);

        buttonPanel.add(compileButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(gameSubmitButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(gameNextButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(gameMessageLabel);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBackground(new Color(240, 248, 255));
        JLabel outputLabel = new JLabel("📤 실행 결과:");
        outputLabel.setFont(new Font("Arial", Font.BOLD, 12));
        outputLabel.setForeground(Color.BLACK);
        outputPanel.add(outputLabel, BorderLayout.NORTH);

        gameResultArea = new JTextArea(6, 100);
        gameResultArea.setFont(new Font("Courier", Font.PLAIN, 11));
        gameResultArea.setForeground(Color.BLACK);
        gameResultArea.setEditable(false);
        gameResultArea.setBackground(new Color(240, 240, 240));
        gameResultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gameResultArea.setLineWrap(true);
        gameResultArea.setWrapStyleWord(true);
        JScrollPane outputScroll = new JScrollPane(gameResultArea);
        outputPanel.add(outputScroll, BorderLayout.CENTER);

        bottomPanel.add(outputPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // 저장 (나중에 사용하기 위해)
        this.gamePanel = panel;

        // 컴파일 버튼 액션 리스너
        compileButton.addActionListener(e -> {
            String code = gameCodeArea.getText();
            if (code.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "코드를 입력하세요!");
                return;
            }

            compileButton.setEnabled(false);
            compileButton.setText("⏳ 실행 중...");
            gameResultArea.setText("");

            String userInput = gameInputField.getText().trim();
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    if (!userInput.isEmpty()) {
                        return ((HandCodingGameService) gameService).compileAndExecute(code, userInput);
                    } else {
                        return ((HandCodingGameService) gameService).compileAndExecute(code);
                    }
                }

                @Override
                protected void done() {
                    try {
                        String result = get();
                        gameResultArea.setText(result);
                        gameMessageLabel.setText("✅ 실행 성공");
                        gameMessageLabel.setForeground(new Color(50, 205, 50));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(HandCodingGameGUI.this,
                            ex.getMessage(), "컴파일 오류", JOptionPane.ERROR_MESSAGE);
                        gameResultArea.setText("");
                        gameMessageLabel.setText("❌ 컴파일 오류");
                        gameMessageLabel.setForeground(new Color(220, 20, 60));
                    } finally {
                        compileButton.setEnabled(true);
                        compileButton.setText("🔨 컴파일");
                    }
                }
            };
            worker.execute();
        });

        // 정답 확인 버튼 액션 리스너
        gameSubmitButton.addActionListener(e -> {
            String code = gameCodeArea.getText();
            if (code.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "코드를 입력하세요!");
                return;
            }

            gameSubmitButton.setEnabled(false);

            // 백그라운드에서 실행
            String userInput = gameInputField.getText().trim();
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return gameService.submitAnswer(
                        currentGameId,
                        currentQuestion.questionId(),
                        code
                    );
                }

                @Override
                protected void done() {
                    try {
                        boolean isCorrect = get();
                        String explanation = gameService.getExplanation(currentGameId);

                        lastAnswerCorrect = isCorrect;

                        if (isCorrect) {
                            gameMessageLabel.setText("✅ 정답입니다! (+10점) - 다음으로 넘어가세요");
                            gameMessageLabel.setForeground(new Color(50, 205, 50));
                            gameNextButton.setText("➡️ 다음");
                            gameNextButton.setEnabled(true);
                            gameSubmitButton.setEnabled(false);
                        } else {
                            gameMessageLabel.setText("❌ 오답입니다 (-10점) - 건너뛰거나 다시 시도하세요");
                            gameMessageLabel.setForeground(new Color(220, 20, 60));
                            gameNextButton.setText("⏭️ 건너뛰기");
                            gameNextButton.setEnabled(true);
                            gameSubmitButton.setEnabled(true);
                        }

                        updateGameScreen();

                        // 게이지가 0이면 게임 종료
                        int currentGauge = ((HandCodingGameService) gameService).getSessionGauge(currentGameId);
                        if (currentGauge <= 0) {
                            JOptionPane.showMessageDialog(HandCodingGameGUI.this,
                                "0이 되면 게임을 종료합니다.",
                                "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                            try {
                                gameService.endGame(currentGameId);
                                cardLayout.show(mainPanel, "CHAPTER");
                                return;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }

                        JOptionPane.showMessageDialog(HandCodingGameGUI.this,
                            explanation, "채점 결과", JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception ex) {
                        gameMessageLabel.setText("❌ 오류: " + ex.getMessage());
                        gameMessageLabel.setForeground(Color.RED);
                        gameSubmitButton.setEnabled(true);
                    }
                }
            };
            worker.execute();
        });

        gameNextButton.addActionListener(e -> {
            try {
                // 건너뛰기인 경우 -15점 처리
                if (!lastAnswerCorrect) {
                    ((HandCodingGameService) gameService).applySkipPenalty(currentGameId);

                    // 건너뛰기 후 게이지 확인
                    int currentGauge = ((HandCodingGameService) gameService).getSessionGauge(currentGameId);
                    if (currentGauge <= 0) {
                        JOptionPane.showMessageDialog(this,
                            "0이 되면 게임을 종료합니다.",
                            "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                        gameService.endGame(currentGameId);
                        cardLayout.show(mainPanel, "CHAPTER");
                        return;
                    }
                }

                Question nextQuestion = gameService.nextQuestion(currentGameId);
                if (nextQuestion == null) {
                    showResult();
                    cardLayout.show(mainPanel, "RESULT");
                } else {
                    currentQuestion = nextQuestion;
                    updateGameScreen();
                    gameCodeArea.setText("");
                    gameInputField.setText("");
                    gameMessageLabel.setText("");
                    gameSubmitButton.setEnabled(true);
                    gameNextButton.setEnabled(false);
                    gameResultArea.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage());
            }
        });

        return panel;
    }

    // ==================== 결과 화면 ====================
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // 제목
        JLabel titleLabel = new JLabel("🎮 게임 완료!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 248, 255));
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // 결과 정보
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        JLabel scoreLabel = new JLabel("최종 점수: -- / 100");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel accuracyLabel = new JLabel("정확도: --%");
        accuracyLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        accuracyLabel.setForeground(Color.BLACK);
        accuracyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel starsLabel = new JLabel("별: ");
        starsLabel.setFont(new Font("Arial", Font.BOLD, 32));
        starsLabel.setForeground(Color.BLACK);
        starsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("상태: ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(scoreLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(accuracyLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(starsLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(statusLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        // 버튼
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JButton homeButton = new JButton("🏠 메인 메뉴");
        homeButton.setFont(new Font("Arial", Font.BOLD, 16));
        homeButton.setBackground(new Color(70, 130, 180));
        homeButton.setForeground(Color.BLACK);
        homeButton.setFocusPainted(false);
        homeButton.setPreferredSize(new Dimension(150, 50));

        homeButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "CHAPTER");
        });

        buttonPanel.add(homeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 나중에 업데이트하기 위해 저장
        this.resultPanel = panel;
        panel.putClientProperty("scoreLabel", scoreLabel);
        panel.putClientProperty("accuracyLabel", accuracyLabel);
        panel.putClientProperty("starsLabel", starsLabel);
        panel.putClientProperty("statusLabel", statusLabel);

        return panel;
    }

    // ==================== 헬퍼 메서드 ====================

    private void startGame(String userId, int chapterId) {
        Game game = gameService.startGame(userId, chapterId);
        currentGameId = game.gameId();
        currentQuestion = gameService.getCurrentQuestion(currentGameId);
    }

    private void showGameHelp() {
        String helpMessage = """
                🎮 HAND CODING 게임 도움말

                ✏️ 게임 방법:
                1. 왼쪽 문제를 읽고 오른쪽에 Java 코드를 작성하세요
                2. 코드를 먼저 컴파일해서 테스트할 수 있습니다
                3. 정답이라고 생각하면 '제출' 버튼을 누르세요

                🔘 버튼 설명:
                • 🔨 컴파일: 코드를 컴파일하고 결과를 확인합니다 (점수 변화 없음)
                • ✅ 제출: 정답을 확인하고 게이지를 변경합니다
                • ➡️ 다음/⏭️ 건너뛰기: 다음 문제로 진행합니다

                📊 점수 시스템:
                • 정답: +10점
                • 오답/오류: -10점
                • 건너뛰기: -15점

                ⭐ 별 획득:
                • 게이지 81~100: ⭐⭐⭐ (3개)
                • 게이지 51~80: ⭐⭐ (2개)
                • 게이지 0~50: ⭐ (1개)

                💡 팁:
                - 컴파일 버튼으로 마음껏 테스트할 수 있습니다
                - 정답을 틀리면 더 신중하게 접근하세요!
                - 건너뛰기는 큰 페널티이므로 신중히 결정하세요
                """;

        JOptionPane.showMessageDialog(this, helpMessage, "📖 게임 도움말", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateGameScreen() {
        if (currentQuestion != null) {
            gameProblemArea.setText(currentQuestion.prompt());
            gameResultArea.setText(currentQuestion.code());
        }

        // 게이지 및 진행도 업데이트
        try {
            Game game = gameService.getGame(currentGameId);
            int gauge = game.currentScore();
            int currentIdx = game.currentQuestionIndex();
            int totalQuestions = game.totalQuestionCount();

            gameGaugeBar.setValue(gauge);
            gameGaugeBar.setString(gauge + "/100");
            gameGaugeLabel.setText(gauge + "/100");
            gameProgressLabel.setText(String.format("진행도: %d / %d", currentIdx + 1, totalQuestions));
        } catch (Exception e) {
            System.err.println("게이지 업데이트 실패: " + e.getMessage());
        }

        // 타이머 시작
        startGameTimer();
    }

    private void startGameTimer() {
        // 이전 타이머 중지
        if (gameTimer != null) {
            gameTimer.stop();
        }

        final boolean[] timeoutHandled = {false};  // 시간 초과 처리 여부

        // 1초마다 타이머 업데이트
        gameTimer = new javax.swing.Timer(1000, e -> {
            try {
                HandCodingGameService service = (HandCodingGameService) gameService;
                long timeRemaining = service.getSessionQuestionTimeRemaining(currentGameId);

                long minutes = timeRemaining / 60;
                long seconds = timeRemaining % 60;

                String timeStr = String.format("⏱️ 남은 시간: %d:%02d", minutes, seconds);
                gameTimeLabel.setText(timeStr);

                // 시간 초과 처리
                if (timeRemaining <= 0 && !timeoutHandled[0]) {
                    timeoutHandled[0] = true;
                    gameTimer.stop();
                    gameTimeLabel.setForeground(new Color(220, 20, 60));  // 빨강

                    // 자동 오답 처리
                    String code = gameCodeArea.getText();
                    String userInput = gameInputField.getText().trim();

                    gameSubmitButton.setEnabled(false);
                    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            return gameService.submitAnswer(
                                currentGameId,
                                currentQuestion.questionId(),
                                code
                            );
                        }

                        @Override
                        protected void done() {
                            try {
                                String explanation = gameService.getExplanation(currentGameId);
                                gameMessageLabel.setText("⏰ 시간 초과! 오답 처리됩니다 (-10점)");
                                gameMessageLabel.setForeground(new Color(220, 20, 60));

                                JOptionPane.showMessageDialog(HandCodingGameGUI.this,
                                    "⏰ 시간 초과!\n오답 처리되었습니다 (-10점)\n\n" + explanation,
                                    "시간 초과", JOptionPane.WARNING_MESSAGE);

                                // 게이지 업데이트 및 다음 버튼 활성화
                                updateGameScreen();

                                // 게이지가 0이면 게임 종료
                                int timeoutGauge = ((HandCodingGameService) gameService).getSessionGauge(currentGameId);
                                if (timeoutGauge <= 0) {
                                    JOptionPane.showMessageDialog(HandCodingGameGUI.this,
                                        "0이 되면 게임을 종료합니다.",
                                        "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                                    try {
                                        gameService.endGame(currentGameId);
                                        cardLayout.show(mainPanel, "CHAPTER");
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    // 다음 버튼 활성화
                                    gameNextButton.setText("➡️ 다음");
                                    gameNextButton.setEnabled(true);
                                }
                            } catch (Exception ex) {
                                gameMessageLabel.setText("❌ 오류: " + ex.getMessage());
                                gameMessageLabel.setForeground(Color.RED);
                                gameSubmitButton.setEnabled(true);
                            }
                        }
                    };
                    worker.execute();
                } else if (timeRemaining <= 60 && timeRemaining > 0) {
                    gameTimeLabel.setForeground(new Color(255, 165, 0));  // 주황
                } else if (timeRemaining > 60) {
                    gameTimeLabel.setForeground(new Color(70, 130, 180));  // 파랑
                }
            } catch (Exception ex) {
                System.err.println("타이머 업데이트 실패: " + ex.getMessage());
            }
        });
        gameTimer.start();
    }

    private void showResult() throws IOException {
        currentResult = gameService.endGame(currentGameId);

        JLabel scoreLabel = (JLabel) resultPanel.getClientProperty("scoreLabel");
        JLabel accuracyLabel = (JLabel) resultPanel.getClientProperty("accuracyLabel");
        JLabel starsLabel = (JLabel) resultPanel.getClientProperty("starsLabel");
        JLabel statusLabel = (JLabel) resultPanel.getClientProperty("statusLabel");

        scoreLabel.setText(String.format("최종 점수: %d / 100", currentResult.finalScore()));
        accuracyLabel.setText(String.format("정확도: %.1f%%", currentResult.accuracyPercent()));
        starsLabel.setText("별: " + "⭐".repeat(currentResult.stars()));

        if (currentResult.cleared()) {
            statusLabel.setText("✅ 클리어 성공!");
            statusLabel.setForeground(new Color(50, 205, 50));
        } else if (currentResult.aborted()) {
            statusLabel.setText("⏹️ 중도 종료");
            statusLabel.setForeground(new Color(255, 165, 0));
        } else {
            statusLabel.setText("❌ 미클리어");
            statusLabel.setForeground(new Color(220, 20, 60));
        }
    }

    public static void main(String[] args) {
        try {
            // 문제 로드
            List<Question> questions = loadQuestions();

            // GameService 생성
            GameService gameService = new HandCodingGameService(questions);

            // GUI 시작
            SwingUtilities.invokeLater(() -> {
                new HandCodingGameGUI(gameService);
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "오류: " + e.getMessage());
        }
    }

    private static List<Question> loadQuestions() throws IOException {
        List<Question> questions = new ArrayList<>();
        java.nio.file.Path path = java.nio.file.Paths.get("data/questions.txt");

        if (!java.nio.file.Files.exists(path)) {
            throw new IOException("data/questions.txt 파일이 없습니다!");
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;

                String questionId = parts[0].trim();
                QuestionType type = QuestionType.valueOf(parts[1].trim());
                String prompt = parts[2].trim().replace("\\n", "\n");
                String code = parts[3].trim().replace("\\n", "\n");
                String inputData = parts.length > 5 ? parts[4].trim().replace("\\n", "\n") : "";

                Question question = new Question(
                    questionId, type, prompt, code, Collections.emptyList()
                );
                questions.add(question);
            }
        }

        return questions;
    }
}
