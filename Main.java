import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import static com.dsaPROJECT.TicTacToe.*;

public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    boolean playAgain;
    do {
        char[][] board = initializeBoard();
        LinkedList<Move> moveHistory = new LinkedList<>();
        Queue<Move> recentMoves = new LinkedList<>();
        Stack<Move> undoStackX = new Stack<>();
        Stack<Move> undoStackO = new Stack<>();
        int undoCountX = MAX_UNDO;
        int undoCountO = MAX_UNDO;
        char player = 'X';
        boolean gameOver = false;

        while (!gameOver) {
            printBoard(board);
            System.out.printf("Player %s enter row and column (0, 1, or 2), 'u' to undo, or 'v' to view recent moves: ",player);
            String input = scanner.next();

            if (input.equalsIgnoreCase("u")) {
                handleUndo(player, board, moveHistory, recentMoves, undoStackX, undoStackO, undoCountX, undoCountO);
                if (player == 'X') undoCountX--;
                else undoCountO--;
                player = switchPlayer(player);
            }
            else if (input.equalsIgnoreCase("v")) {
                displayRecentMoves(recentMoves);
            }
            else {
                gameOver = processMove(input, scanner, board, moveHistory, recentMoves, undoStackX, undoStackO, player);
                if (!gameOver)
                    player = switchPlayer(player);
            }
        }

        System.out.println("Game Over! Here is the move history:");
        displayMoveHistory(moveHistory);
        System.out.print("Do you want to play again? (yes/no): ");
        playAgain = scanner.next().equalsIgnoreCase("yes");
    } while (playAgain);

    scanner.close();
}