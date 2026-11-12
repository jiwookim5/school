package kr.ac.kyonggi.javaquest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import teamapi.v1.*;

final class HandCodingGamePanel extends GamePanelBase {
    private GameService gameService;
    private String currentGameId;
    private Question currentQuestion;

    private JTextArea gameProblemArea;
    private JTextArea gameResultArea;
    private JTextArea gameCodeArea;
    private JTextField gameInputField;
    private JButton gameSubmitButton;
    private JButton gameCompileButton;
    private JButton gameNextButton;
    private JLabel gameMessageLabel;
    private JLabel gameGaugeLabel;
    private JProgressBar gameGaugeBar;
    private boolean lastAnswerCorrect;

    HandCodingGamePanel(JavaQuestFrame frame, LearningService service, Mission mission) {
        super(frame, service, mission);
        try {
            List<Question> questions = loadQuestions();
            gameService = new HandCodingGameService(questions);
            setupGameUI();
            startGame();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "게임 로드 실패: " + e.getMessage());
        }
    }

    private List<Question> loadQuestions() throws IOException {
        List<Question> questions = new ArrayList<>();
        Path path = Paths.get("data/questions.txt");

        if (!Files.exists(path)) {
            throw new FileNotFoundException("data/questions.txt를 찾을 수 없습니다");
        }

        BufferedReader reader = Files.newBufferedReader(path);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.trim().isEmpty()) continue;

            String[] parts = line.split("\\|");
            if (parts.length < 4) continue;

            String questionId = parts[0].trim();
            QuestionType type = QuestionType.valueOf(parts[1].trim());
            String prompt = parts[2].trim().replace("\\n", "\n");
            String code = parts[3].trim();

            Question question = new Question(questionId, type, prompt, code, Collections.emptyList());
            questions.add(question);
        }
        reader.close();
        return questions;
    }

    private void setupGameUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        // 상단: 게이지 + 진행도
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 248, 255));
        JLabel progressLabel = new JLabel("진행도: ");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 13));
        progressLabel.setForeground(new Color(70, 130, 180));
        statusPanel.add(progressLabel);
        headerPanel.add(statusPanel, BorderLayout.WEST);

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

        add(headerPanel, BorderLayout.NORTH);

        // 중앙: 좌우 분할 (문제 1/3 | 코드 2/3)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(15, 0, 15, 0);

        // 왼쪽: 문제 설명
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
        gbc.weightx = 0.33;
        contentPanel.add(leftPanel, gbc);

        // 오른쪽: 코드 입력
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
        gbc.weightx = 0.67;
        contentPanel.add(rightPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // 하단: 버튼 + 메시지 + 출력
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        gameCompileButton = new JButton("🔨 컴파일");
        gameCompileButton.setFont(new Font("Arial", Font.BOLD, 14));
        gameCompileButton.setBackground(new Color(100, 149, 237));
        gameCompileButton.setForeground(Color.BLACK);
        gameCompileButton.setFocusPainted(false);
        gameCompileButton.setPreferredSize(new Dimension(120, 40));

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

        buttonPanel.add(gameCompileButton);
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

        add(bottomPanel, BorderLayout.SOUTH);

        // 버튼 액션 리스너
        gameCompileButton.addActionListener(e -> compileButtonClicked());
        gameSubmitButton.addActionListener(e -> submitButtonClicked());
        gameNextButton.addActionListener(e -> nextButtonClicked());
    }

    private void startGame() {
        try {
            Game game = gameService.startGame("player", 1);
            currentGameId = game.gameId();
            currentQuestion = gameService.getCurrentQuestion(currentGameId);
            updateGameScreen();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "게임 시작 오류: " + e.getMessage());
        }
    }

    private void compileButtonClicked() {
        String code = gameCodeArea.getText();
        if (code.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "코드를 입력하세요!");
            return;
        }

        gameCompileButton.setEnabled(false);
        gameCompileButton.setText("⏳ 실행 중...");
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
                    JOptionPane.showMessageDialog(HandCodingGamePanel.this,
                        ex.getMessage(), "컴파일 오류", JOptionPane.ERROR_MESSAGE);
                    gameResultArea.setText("");
                    gameMessageLabel.setText("❌ 컴파일 오류");
                    gameMessageLabel.setForeground(new Color(220, 20, 60));
                } finally {
                    gameCompileButton.setEnabled(true);
                    gameCompileButton.setText("🔨 컴파일");
                }
            }
        };
        worker.execute();
    }

    private void submitButtonClicked() {
        String code = gameCodeArea.getText();
        if (code.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "코드를 입력하세요!");
            return;
        }

        gameSubmitButton.setEnabled(false);

        String userInput = gameInputField.getText().trim();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return gameService.submitAnswer(
                    currentGameId,
                    currentQuestion.questionId(),
                    code,
                    userInput
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

                    int currentGauge = ((HandCodingGameService) gameService).getSessionGauge(currentGameId);
                    if (currentGauge <= 0) {
                        JOptionPane.showMessageDialog(HandCodingGamePanel.this,
                            "게이지가 0이 되면 게임을 종료합니다.",
                            "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                        try {
                            gameService.endGame(currentGameId);
                            complete(1000, 3);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        return;
                    }

                    JOptionPane.showMessageDialog(HandCodingGamePanel.this,
                        explanation, "채점 결과", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    gameMessageLabel.setText("❌ 오류: " + ex.getMessage());
                    gameMessageLabel.setForeground(Color.RED);
                    gameSubmitButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void nextButtonClicked() {
        try {
            if (!lastAnswerCorrect) {
                ((HandCodingGameService) gameService).applySkipPenalty(currentGameId);
                int currentGauge = ((HandCodingGameService) gameService).getSessionGauge(currentGameId);
                if (currentGauge <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "게이지가 0이 되면 게임을 종료합니다.",
                        "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                    gameService.endGame(currentGameId);
                    complete(100, 1);
                    return;
                }
            }

            Question nextQuestion = gameService.nextQuestion(currentGameId);
            if (nextQuestion == null) {
                complete(1000, 3);
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
    }

    private void updateGameScreen() {
        if (currentQuestion != null) {
            gameProblemArea.setText(currentQuestion.prompt());
        }

        try {
            Game game = gameService.getGame(currentGameId);
            int gauge = game.currentScore();
            gameGaugeBar.setValue(gauge);
            gameGaugeBar.setString(gauge + "/100");
            gameGaugeLabel.setText(gauge + "/100");
        } catch (Exception e) {
            System.err.println("게이지 업데이트 실패: " + e.getMessage());
        }
    }
}
