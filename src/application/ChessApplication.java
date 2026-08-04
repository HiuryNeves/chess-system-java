package application;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the desktop version of the chess game.
 */
public class ChessApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();
            new ChessFrame().setVisible(true);
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            // The application can still run with Swing's default look and feel.
        }
    }
}
