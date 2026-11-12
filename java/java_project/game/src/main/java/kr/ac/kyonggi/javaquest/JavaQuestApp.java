package kr.ac.kyonggi.javaquest;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Application entry point. */
public final class JavaQuestApp {
    private JavaQuestApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applyLookAndFeel();
            JavaQuestFrame frame = new JavaQuestFrame(LearningData.createDemoService());
            frame.setVisible(true);
        });
    }

    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing's default look and feel remains usable if the system one is unavailable.
        }
    }
}
