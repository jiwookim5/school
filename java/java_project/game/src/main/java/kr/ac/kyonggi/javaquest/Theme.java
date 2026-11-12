package kr.ac.kyonggi.javaquest;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

/** Shared visual language for the Swing screens. */
public final class Theme {
    public static final Color INK = new Color(29, 38, 53);
    public static final Color MUTED = new Color(104, 117, 135);
    public static final Color BACKGROUND = new Color(247, 249, 252);
    public static final Color SURFACE = Color.WHITE;
    public static final Color LINE = new Color(224, 230, 238);
    public static final Color PURPLE = new Color(101, 81, 216);
    public static final Color PURPLE_DARK = new Color(75, 58, 170);
    public static final Color GREEN = new Color(39, 170, 105);
    public static final Color YELLOW = new Color(255, 186, 45);
    public static final Color RED = new Color(226, 83, 83);
    private static final BufferedImage GIRYONG = loadGiryong();

    private Theme() {
    }

    public static JLabel label(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Malgun Gothic", Font.PLAIN, size));
        return label;
    }

    public static JLabel title(String text) {
        JLabel label = label(text, 28, INK);
        label.setFont(new Font("Malgun Gothic", Font.BOLD, 28));
        return label;
    }

    public static JLabel heading(String text) {
        JLabel label = label(text, 18, INK);
        label.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        return label;
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        return panel;
    }

    public static JButton button(String text) {
        JButton button = new JButton(text);
        // Keep the custom project colors consistent across Windows look-and-feels.
        button.setUI(new BasicButtonUI());
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PURPLE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton ghostButton(String text) {
        JButton button = button(text);
        button.setForeground(INK);
        button.setBackground(SURFACE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(9, 15, 9, 15)));
        return button;
    }

    public static Border padding(int top, int left, int bottom, int right) {
        return BorderFactory.createEmptyBorder(top, left, bottom, right);
    }

    public static void drawGiryong(Graphics2D graphics, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        if (GIRYONG != null) {
            double scale = Math.min(width / (double) GIRYONG.getWidth(), height / (double) GIRYONG.getHeight());
            int drawWidth = Math.max(1, (int) Math.round(GIRYONG.getWidth() * scale));
            int drawHeight = Math.max(1, (int) Math.round(GIRYONG.getHeight() * scale));
            int drawX = x + (width - drawWidth) / 2;
            int drawY = y + (height - drawHeight) / 2;
            Graphics2D mascotGraphics = (Graphics2D) graphics.create();
            mascotGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            mascotGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            mascotGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            mascotGraphics.drawImage(GIRYONG, drawX, drawY, drawWidth, drawHeight, null);
            mascotGraphics.dispose();
            return;
        }
        graphics.setColor(new Color(91, 193, 126));
        graphics.fillOval(x + width / 8, y + height / 4, width * 3 / 4, height * 2 / 3);
        graphics.setColor(new Color(81, 111, 205));
        graphics.fillOval(x + width / 4, y + height / 10, width / 6, height / 4);
        graphics.fillOval(x + width * 3 / 5, y + height / 10, width / 6, height / 4);
        graphics.setColor(Color.WHITE);
        graphics.fillOval(x + width / 3, y + height / 2, width / 7, height / 6);
        graphics.fillOval(x + width / 2, y + height / 2, width / 7, height / 6);
    }

    private static BufferedImage loadGiryong() {
        try (InputStream resource = Theme.class.getResourceAsStream("/kr/ac/kyonggi/javaquest/giryong.png")) {
            if (resource != null) {
                return ImageIO.read(resource);
            }
            Path sourceFile = Path.of("src", "main", "resources", "kr", "ac", "kyonggi", "javaquest", "giryong.png");
            return Files.isRegularFile(sourceFile) ? ImageIO.read(sourceFile.toFile()) : null;
        } catch (IOException exception) {
            return null;
        }
    }

    public static final class Mascot extends JPanel {
        public Mascot() {
            setOpaque(false);
            setPreferredSize(new Dimension(125, 125));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            drawGiryong(g, 0, 0, getWidth(), getHeight());
            g.dispose();
        }
    }
}
