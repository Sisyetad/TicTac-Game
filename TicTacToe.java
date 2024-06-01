package com.dsaPROJECT;
import java.util.*;
public class TicTacToe {

    public static char[][] initializeBoard() {
        char[][] board = new char[3][3];
        for (char[] chars : board) {
            Arrays.fill(chars, ' ');
        }
        return board;
    }
    public static void printBoard(char[][] board) {
        System.out.println("  0   1   2 ");
        for (int row = 0; row < board.length; row++) {
            System.out.printf("%d ",row);
            for (int col = 0; col < board[row].length; col++) {
                System.out.print(board[row][col]);
                if (col < board[row].length - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            if (row < board.length - 1) {
                System.out.println("  ---------");
            }
        }
    }

    public static class Move {
            int row,col;
            char player;
            Move(int row, int col, char player) {
                this.row = row;
                this.col = col;
                this.player = player;
            }
        }
        public static final int MAX_UNDO = 2;// number of moves to undo in one game
        static final int HISTORY_SIZE = 5; // Number of moves to keep in the history queue



        public static void handleUndo(char player, char[][] board, LinkedList<Move> moveHistory, Queue<Move> recentMoves, Stack<Move> undoStackX, Stack<Move> undoStackO, int undoCountX, int undoCountO) {
            if (player == 'X' && !undoStackX.isEmpty() && undoCountX > 0) {
                undoMove(board, moveHistory, recentMoves, undoStackX);
                System.out.printf("Undo successful. Player X has %d undo left.\n",undoCountX - 1);
            } else if (player == 'O' && !undoStackO.isEmpty() && undoCountO > 0) {
                undoMove(board, moveHistory, recentMoves, undoStackO);
                System.out.printf("Undo successful. Player O has %d undo left.\n",undoCountX - 1);
            } else {
                System.out.println("No moves to undo or no undo left!");
            }
        }

        private static void undoMove(char[][] board, LinkedList<Move> moveHistory, Queue<Move> recentMoves, Stack<Move> undoStack) {
            Move lastMove = undoStack.pop();
            board[lastMove.row][lastMove.col] = ' ';
            moveHistory.removeLast();
            recentMoves.remove(lastMove);
        }

    static boolean isValidMove(int row, int col, char[][] board) {
                return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == ' ';
    }


    static void placeMove(char[][] board, int row, int col, char player, LinkedList<Move> moveHistory, Queue<Move> recentMoves, Stack<Move> undoStackX, Stack<Move> undoStackO) {
        board[row][col] = player;
        Move move = new Move(row, col, player);
        moveHistory.add(move);
        if (player == 'X') {
            undoStackX.push(move);
        }
        else {
            undoStackO.push(move);
        }
        recentMoves.add(move);
        if (recentMoves.size() > HISTORY_SIZE) {
            recentMoves.poll();
        }
    }


    public static boolean processMove(String input, Scanner scanner, char[][] board, LinkedList<Move> moveHistory, Queue<Move> recentMoves, Stack<Move> undoStackX, Stack<Move> undoStackO, char player) {
            try {
                int row = Integer.parseInt(input);
                int col = scanner.nextInt();
                if (isValidMove(row, col, board)) {
                    placeMove(board, row, col, player, moveHistory, recentMoves, undoStackX, undoStackO);
                    return checkGameOver(board, player);
                }
                else {
                    System.out.println("Invalid move. Try again!");
                    return false;
                }
            }
            catch (NumberFormatException | InputMismatchException e) {
                System.out.println("Invalid input. Enter row and column as given numbers.");
                scanner.nextLine(); // Clear the invalid input
                return false;
            }
        }

    public static boolean haveWon(char[][] board, char player) {
        // Check the rows
        for (char[] chars : board) {
            if (chars[0] == player && chars[1] == player && chars[2] == player) {
                return true;
            }
        }
        // Check the columns
        for (int col = 0; col < board[0].length; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                return true;
            }
        }
        // Check the diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        else return board[0][2] == player && board[1][1] == player && board[2][0] == player;
    }

        private static boolean checkGameOver(char[][] board, char player) {
            if (haveWon(board, player)) {
                printBoard(board);
                System.out.printf("Player %s has won!\n",player);
                return true;
            }
            else if (isBoardFull(board)) {
                printBoard(board);
                System.out.println("The game is a draw!");
                return true;
            }
            return false;
        }

        public static char switchPlayer(char currentPlayer) {

                return currentPlayer == 'X' ? 'O' : 'X';

        }

        public static boolean isBoardFull(char[][] board) {
            for (char[] chars : board) {
                for (char aChar : chars) {
                    if (aChar == ' ') {
                        return false;
                    }
                }
            }
            return true;
        }

        public static void displayMoveHistory(LinkedList<Move> moveHistory) {
            for (Move move : moveHistory) {
                System.out.printf("Player %s moved to (%d, %d)\n",move.player,move.row,move.col);
            }
        }

        public static void displayRecentMoves(Queue<Move> recentMoves) {
            for (Move move : recentMoves) {
                System.out.printf("Player %s moved to (%d, %d)\n",move.player,move.row,move.col);
            }
        }

}
