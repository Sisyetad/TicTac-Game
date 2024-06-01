package com.dsaPROJECT;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class TicTacToeGUI extends JFrame {
    // Constants for maximum undo allowed and size of recent move history
    private static final int MAX_UNDO = 2;
    private static final int HISTORY_SIZE = 5;

    // Game state variables
    private char[][] board;
    private char currentPlayer;
    private LinkedList<Move> moveHistory;   // History of all moves made in the game
    private Queue<Move> recentMoves;   // Queue to keep track of recent move

    // Stacks to store moves made by one of the players for undo operations
    private Stack<Move> undoStackX;
    private Stack<Move> undoStackO;
    // The undo counter for both players   
    private int undoCountX;
    private int undoCountO;

    private boolean gameOver;  // Flag to check if the game is over

    // Button variables
    private JButton[][] buttons;
    private final JLabel statusLabel;  // label to display the game status (e.g., current player's turn or Player X's turn)

    // Inner class to represent a move in the game
    static class Move {
        int row;
        int col;
        char player;

        Move(int row, int col, char player) {
            this.row = row;
            this.col = col;
            this.player = player;
        }
    }

    // Constructor to set up the GUI
    public TicTacToeGUI() {
        // Set the title, size, and default close operation of the window
        setTitle("Tic-Tac-Toe");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize panels for the board and controls
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        add(boardPanel, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel();
        add(controlPanel, BorderLayout.SOUTH);
        statusLabel = new JLabel("Player X's turn");
        add(statusLabel, BorderLayout.NORTH);

        // Initialize game state and UI components
        initializeGame();
        initializeButtons(boardPanel);
        initializeControlButtons(controlPanel);
        setVisible(true);
    }



    // Initialize the game state variables
    private void initializeGame() {

        // First initialize the board
        board = new char[3][3];
        for (char[] chars : board) {
            Arrays.fill(chars, ' ');
        }

        currentPlayer = 'X';
        moveHistory = new LinkedList<>();
        recentMoves = new LinkedList<>();
        undoStackX = new Stack<>();
        undoStackO = new Stack<>();
        undoCountX = MAX_UNDO;
        undoCountO = MAX_UNDO;
        gameOver = false;
    }



    // Initialize the buttons on the game board
    private void initializeButtons(JPanel boardPanel) {
        buttons = new JButton[3][3];  // Create a 3x3 array of buttons
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton button = new JButton("");  // Create a new button for each cell
                button.setFont(new Font("Arial", Font.PLAIN, 80));  // Set the font size
                button.setFocusPainted(false);   // Remove focus painting

                int finalRow = row;
                int finalCol = col;

                // Add action listener to handle button click
                button.addActionListener(_ -> handleButtonClick(finalRow, finalCol));
                buttons[row][col] = button; // Add button to the buttons array
                boardPanel.add(button); // Add button to the board panel
            }
        }
    }



    // Initialize the control buttons (Undo, View Recent Moves, View All Moves, Play Again)
    private void initializeControlButtons(JPanel controlPanel) {
        JButton undoButton = new JButton("Undo"); // Create Undo button
        undoButton.addActionListener(_ -> handleUndo()); // Add action listener for Undo button
        controlPanel.add(undoButton); // Add Undo button to control panel

        JButton viewMovesButton = new JButton("View Recent Moves"); // Create View Recent Moves button
        viewMovesButton.addActionListener(_ -> displayRecentMoves()); // Add action listener for View Recent Moves button
        controlPanel.add(viewMovesButton); // Add View Recent Moves button to control panel

        JButton viewAllMovesButton = new JButton("View All Moves"); // Create View All Moves button
        viewAllMovesButton.addActionListener(_ -> displayAllMoves()); // Add action listener for View All Moves button
        controlPanel.add(viewAllMovesButton); // Add View All Moves button to control panel

        JButton playAgainButton = new JButton("Play Again"); // Create Play Again button
        playAgainButton.addActionListener(_ -> playAgain()); // Add action listener for Play Again button
        controlPanel.add(playAgainButton); // Add Play Again button to control panel
    }




    // Handle button click events on the game board
    private void handleButtonClick(int row, int col) {
        // Ignore clicks if the game is over or the cell is already occupied
        if (gameOver || board[row][col] != ' ') {
            return;
        }

        // Update the board with the current player's move
        board[row][col] = currentPlayer;
        buttons[row][col].setText(String.valueOf(currentPlayer));

        // object named move to be stored in the Linked list, Queue, and Stacks
        Move move = new Move(row, col, currentPlayer);
        moveHistory.add(move);
        if (currentPlayer == 'X') {
            undoStackX.push(move);
        } else {
            undoStackO.push(move);
        }

        // Update recent moves queue, removing the oldest move if necessary
        recentMoves.add(move);
        if (recentMoves.size() > HISTORY_SIZE) {
            recentMoves.poll();
        }

        // After placing the Player, We need to check if there is a winner
        // Check for win or draw conditions:

        // Win condition
        if (haveWon(board, currentPlayer)) {
            gameOver = true;
            statusLabel.setText(STR."Player \{currentPlayer} has won!");
            JOptionPane.showMessageDialog(this, STR."Player \{currentPlayer} has won!");

            // Draw Condition
        } else if (isBoardFull(board)) {
            gameOver = true;
            statusLabel.setText("The game is a draw!");
            JOptionPane.showMessageDialog(this, "The game is a draw!");

            // Neither of the two (Game Continues)
        } else {
            // Switch to the other player and update the status label
            currentPlayer = switchPlayer(currentPlayer);
            statusLabel.setText(STR."Player \{currentPlayer}'s turn");
        }
    }




    // Handle the Undo button click event
    private void handleUndo() {
        if (gameOver) {
            JOptionPane.showMessageDialog(this, "Game is over. Can't undo!");
            return;
        }

        // Check if the current player has any undo left and there are moves to undo
        if (currentPlayer == 'X' && !undoStackX.isEmpty() && undoCountX > 0) {
            undoMove(undoStackX);
            undoCountX--;
            statusLabel.setText(STR."Undo successful. Player X has \{undoCountX} undos left.");
        } else if (currentPlayer == 'O' && !undoStackO.isEmpty() && undoCountO > 0) {
            undoMove(undoStackO);
            undoCountO--;
            statusLabel.setText(STR."Undo successful. Player O has \{undoCountO} undos left.");
        } else {
            statusLabel.setText("No moves to undo or no undo left!");
        }
    }


    // Undo the last move from the specified undo stack
    private void undoMove(Stack<Move> undoStack) {
        Move lastMove = undoStack.pop();

        // Updating the board with empty space
        board[lastMove.row][lastMove.col] = ' ';
        buttons[lastMove.row][lastMove.col].setText("");

        // Removing the last move from the Queue and Linked list
        moveHistory.removeLast();
        recentMoves.remove(lastMove);

        // After the Undo We switch the player (Optional)
        currentPlayer = switchPlayer(currentPlayer);
        statusLabel.setText(STR."Player \{currentPlayer}'s turn");
    }


    // Switch the current player
    private char switchPlayer(char currentPlayer) {
        return currentPlayer == 'X' ? 'O' : 'X';
    }


    // Check if the specified player has won the game
    private boolean haveWon(char[][] board, char player) {
        // Check rows, columns, and diagonals
        for (char[] chars : board) {
            if (chars[0] == player && chars[1] == player && chars[2] == player) {
                return true;
            }
        }

        for (int col = 0; col < board[0].length; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                return true;
            }
        }

        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }

        return board[0][2] == player && board[1][1] == player && board[2][0] == player;
    }


    // Check if the board is full (no empty cells)
    private boolean isBoardFull(char[][] board) {
        for (char[] chars : board) {
            for (char aChar : chars) {
                if (aChar == ' ') {
                    return false;
                }
            }
        }
        return true;
    }


    // Display the recent moves in a message dialog
    private void displayRecentMoves() {
        StringBuilder recentMovesStr = new StringBuilder("Recent moves:\n");
        for (Move move : recentMoves) {
            recentMovesStr.append("Player ").append(move.player).append(" moved to (")
                    .append(move.row).append(", ").append(move.col).append(")\n");
        }
        JOptionPane.showMessageDialog(this, recentMovesStr.toString());
    }

    // Display the move history in a message dialog
    private void displayAllMoves() {
        StringBuilder moveHistoryStr = new StringBuilder("Move history:\n");
        for (Move move : moveHistory) {
            moveHistoryStr.append("Player ").append(move.player).append(" moved to (")
                    .append(move.row).append(", ").append(move.col).append(")\n");
        }
        JOptionPane.showMessageDialog(this, moveHistoryStr.toString());
    }

    // Reset the game state and UI for a new game
    private void playAgain() {
        initializeGame(); // Play again is initializing the Game again
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("");
            }
        }
        statusLabel.setText("Player X's turn");
    }



    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGUI::new);
    }
}
