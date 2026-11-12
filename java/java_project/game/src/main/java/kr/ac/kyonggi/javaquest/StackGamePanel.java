package kr.ac.kyonggi.javaquest;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/** Builds a valid code snippet by placing shuffled blocks in execution order. */
final class StackGamePanel extends GamePanelBase {
    private final List<String> answer;
    private final DefaultListModel<String> availableModel = new DefaultListModel<>();
    private final DefaultListModel<String> stackModel = new DefaultListModel<>();
    private final JList<String> available = new JList<>(availableModel);
    private final JList<String> stack = new JList<>(stackModel);
    private final JLabel status = Theme.label("블록을 순서대로 쌓고 완성 여부를 확인하세요.", 14, Theme.MUTED);

    StackGamePanel(JavaQuestFrame frame, LearningService service, Mission mission) {
        super(frame, service, mission);
        answer = mission.stackBlocks();
        List<String> shuffled = new ArrayList<>(answer);
        Collections.shuffle(shuffled);
        shuffled.forEach(availableModel::addElement);
        addGameHeader("아래 블록을 올바른 순서로 쌓아 코드를 완성하세요. 한 줄씩 조립해 봐요.");

        JPanel game = panelCard();
        game.setLayout(new BorderLayout(16, 16));
        game.add(Theme.heading("코드 스택"), BorderLayout.NORTH);

        JPanel columns = new JPanel(new GridLayout(1, 3, 12, 0));
        columns.setOpaque(false);
        columns.add(listPanel("다음 블록", available));
        columns.add(controlPanel());
        columns.add(listPanel("완성 중인 코드", stack));
        game.add(columns, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(status);
        game.add(bottom, BorderLayout.SOUTH);
        add(game);
        add(Box.createVerticalStrut(14));
        JPanel hint = panelCard();
        hint.add(Theme.label("🐉 기룡이의 힌트  " + mission.hint(), 14, Theme.PURPLE_DARK));
        add(hint);
    }

    private JPanel listPanel(String title, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(Theme.label(title, 14, Theme.PURPLE_DARK), BorderLayout.NORTH);
        list.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 14));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel controlPanel() {
        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.add(Box.createVerticalGlue());
        JButton add = Theme.button("추가  ›");
        add.setAlignmentX(CENTER_ALIGNMENT);
        add.addActionListener(event -> addBlock());
        controls.add(add);
        controls.add(Box.createVerticalStrut(10));
        JButton undo = Theme.ghostButton("‹ 되돌리기");
        undo.setAlignmentX(CENTER_ALIGNMENT);
        undo.addActionListener(event -> undoBlock());
        controls.add(undo);
        controls.add(Box.createVerticalStrut(10));
        JButton check = Theme.ghostButton("완성 확인");
        check.setAlignmentX(CENTER_ALIGNMENT);
        check.addActionListener(event -> checkStack());
        controls.add(check);
        controls.add(Box.createVerticalGlue());
        return controls;
    }

    private void addBlock() {
        int index = available.getSelectedIndex();
        if (index < 0) {
            status.setText("왼쪽에서 옮길 코드 블록을 선택하세요.");
            status.setForeground(Theme.RED);
            return;
        }
        stackModel.addElement(availableModel.get(index));
        availableModel.remove(index);
        status.setText("블록을 쌓았어요. 현재 " + stackModel.size() + " / " + answer.size() + "줄");
        status.setForeground(Theme.MUTED);
    }

    private void undoBlock() {
        if (stackModel.isEmpty()) {
            return;
        }
        availableModel.addElement(stackModel.lastElement());
        stackModel.remove(stackModel.size() - 1);
        status.setText("마지막 블록을 되돌렸어요.");
        status.setForeground(Theme.MUTED);
    }

    private void checkStack() {
        if (stackModel.size() != answer.size()) {
            status.setText("아직 빈칸이 있어요. 모든 블록을 쌓아 보세요.");
            status.setForeground(Theme.RED);
            return;
        }
        for (int index = 0; index < answer.size(); index++) {
            if (!answer.get(index).equals(stackModel.get(index))) {
                status.setText((index + 1) + "번째 줄의 순서가 달라요. 되돌려서 다시 쌓아 보세요.");
                status.setForeground(Theme.RED);
                return;
            }
        }
        complete(1100, 3);
    }
}
