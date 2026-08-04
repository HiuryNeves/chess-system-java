package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

/**
 * Swing user interface. It delegates every rule decision to {@link ChessMatch};
 * this class only turns clicks into source and target positions.
 */
public class ChessFrame extends JFrame {

    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECTED_SQUARE = new Color(246, 246, 105);
    private static final Color POSSIBLE_MOVE = new Color(130, 190, 120);

    private final JButton[][] squares = new JButton[8][8];
    private final JLabel status = new JLabel("", SwingConstants.CENTER);
    private final JLabel captured = new JLabel("", SwingConstants.CENTER);
    private final List<ChessPiece> capturedPieces = new ArrayList<>();

    private ChessMatch match = new ChessMatch();
    private ChessPosition selectedSource;
    private boolean[][] possibleMoves;

    public ChessFrame() {
        super("Chess System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(createHeader(), BorderLayout.NORTH);
        add(createBoard(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        pack();
        setLocationByPlatform(true);
        refreshBoard();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        status.setBorder(BorderFactory.createEmptyBorder(8, 8, 2, 8));
        captured.setBorder(BorderFactory.createEmptyBorder(2, 8, 8, 8));
        panel.add(status);
        panel.add(captured);
        return panel;
    }

    private JPanel createBoard() {
        JPanel board = new JPanel(new GridLayout(8, 8));
        board.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                final int boardRow = row;
                final int boardColumn = column;
                JButton square = new JButton();
                square.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 38));
                square.setFocusPainted(false);
                square.setBorderPainted(false);
                square.setOpaque(true);
                square.setPreferredSize(new java.awt.Dimension(74, 74));
                square.addActionListener(event -> selectSquare(boardRow, boardColumn));
                squares[row][column] = square;
                board.add(square);
            }
        }
        return board;
    }

    private JPanel createFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton restart = new JButton("New game");
        restart.addActionListener(event -> restartGame());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        panel.add(restart, BorderLayout.EAST);
        return panel;
    }

    private void selectSquare(int row, int column) {
        if (match.getCheckMate()) {
            return;
        }

        ChessPosition clickedPosition = new ChessPosition((char) ('a' + column), 8 - row);
        try {
            if (selectedSource == null) {
                possibleMoves = match.possibleMoves(clickedPosition);
                selectedSource = clickedPosition;
            } else {
                ChessPiece capturedPiece = match.performChessMove(selectedSource, clickedPosition);
                if (capturedPiece != null) {
                    capturedPieces.add(capturedPiece);
                }
                choosePromotion();
                selectedSource = null;
                possibleMoves = null;
            }
        } catch (ChessException exception) {
            selectedSource = null;
            possibleMoves = null;
            showError(exception.getMessage());
        }
        refreshBoard();
    }

    private void choosePromotion() {
        if (match.getPromoted() == null) {
            return;
        }
        String[] options = { "Queen", "Rook", "Bishop", "Knight" };
        int option = JOptionPane.showOptionDialog(
                this,
                "Choose the promoted piece.",
                "Pawn promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        String type = switch (option) {
            case 1 -> "R";
            case 2 -> "B";
            case 3 -> "N";
            default -> "Q";
        };
        match.replacePromotedPiece(type);
    }

    private void refreshBoard() {
        ChessPiece[][] pieces = match.getPieces();
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                JButton square = squares[row][column];
                square.setText(pieceSymbol(pieces[row][column]));
                square.setBackground(squareColor(row, column));
            }
        }
        updateStatus();
    }

    private Color squareColor(int row, int column) {
        if (selectedSource != null
                && selectedSource.getRow() == 8 - row
                && selectedSource.getColumn() == (char) ('a' + column)) {
            return SELECTED_SQUARE;
        }
        if (possibleMoves != null && possibleMoves[row][column]) {
            return POSSIBLE_MOVE;
        }
        return (row + column) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
    }

    private String pieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return "";
        }
        boolean white = piece.getColor() == chess.Color.WHITE;
        return switch (piece.toString()) {
            case "K" -> white ? "♔" : "♚";
            case "Q" -> white ? "♕" : "♛";
            case "R" -> white ? "♖" : "♜";
            case "B" -> white ? "♗" : "♝";
            case "N" -> white ? "♘" : "♞";
            default -> white ? "♙" : "♟";
        };
    }

    private void updateStatus() {
        if (match.getCheckMate()) {
            status.setText("Checkmate — " + match.getCurrentPlayer() + " wins.");
        } else if (selectedSource != null) {
            status.setText("Choose a highlighted target square.");
        } else {
            String check = match.getCheck() ? " — CHECK" : "";
            status.setText("Turn " + match.getTurn() + ": " + match.getCurrentPlayer() + check);
        }
        captured.setText("Captured: " + capturedPieces.stream().map(this::pieceSymbol).reduce("", String::concat));
    }

    private void restartGame() {
        match = new ChessMatch();
        selectedSource = null;
        possibleMoves = null;
        capturedPieces.clear();
        refreshBoard();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid move", JOptionPane.WARNING_MESSAGE);
    }
}
