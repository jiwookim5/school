package kr.ac.kyonggi.javaquest;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import teamapi.v1.*;

public class HandCodingGameWindow extends JFrame {
    private Scanner scanner;

    public HandCodingGameWindow() {
        setTitle("JAVA QUEST - 코딩 보스 (문제 1)");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            List<Question> questions = loadQuestions();
            showGameUI(questions);
            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "게임 로드 실패: " + e.getMessage());
            dispose();
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
            String explanation = parts.length > 4 ? parts[4].trim() : "";

            Question question = new Question(questionId, type, prompt, code,
                Collections.emptyList());
            questions.add(question);
        }
        reader.close();
        return questions;
    }

    private void showGameUI(List<Question> questions) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 문제 선택 (문제 101만)
        Question problem = questions.stream()
            .filter(q -> q.questionId().equals("101"))
            .findFirst()
            .orElse(questions.get(0));

        // 문제 설명
        JTextArea problemArea = new JTextArea(problem.prompt());
        problemArea.setEditable(false);
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);
        problemArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("문제: " + problem.questionId()));
        panel.add(new JScrollPane(problemArea));
        panel.add(new JLabel("코드 입력:"));

        // 코드 입력
        JTextArea codeArea = new JTextArea(10, 50);
        codeArea.setText("int sum = 0;\nfor (int i = 1; i <= 5; i++) {\n    sum += i;\n}\nSystem.out.println(sum);");
        codeArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 12));

        panel.add(new JScrollPane(codeArea));

        // 제출 버튼
        JButton submitBtn = new JButton("제출");
        submitBtn.addActionListener(e -> {
            String code = codeArea.getText();
            checkAnswer(code, problem.questionId());
        });
        panel.add(submitBtn);

        // 결과
        JLabel resultLabel = new JLabel("");
        panel.add(resultLabel);

        setContentPane(new JScrollPane(panel));
    }

    private void checkAnswer(String code, String questionId) {
        String upper = code.toUpperCase();
        String noSpace = code.replaceAll("\\s+", "");

        if (!upper.contains("FOR")) {
            JOptionPane.showMessageDialog(this, "❌ for 문을 사용해야 합니다");
            return;
        }
        if (!code.contains("sum")) {
            JOptionPane.showMessageDialog(this, "❌ sum 변수를 사용해야 합니다");
            return;
        }
        if (!noSpace.contains("System.out.println(sum)")) {
            JOptionPane.showMessageDialog(this, "❌ System.out.println(sum) 형태로 출력해야 합니다");
            return;
        }

        JOptionPane.showMessageDialog(this, "✅ 정답입니다!\n축하합니다!", "정답", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
