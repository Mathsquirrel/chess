package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessGame.TeamColor.*;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board = new ChessPiece[8][8];;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        // Sets Back Ranks
        for(int i = 0; i < board[0].length; i++){
            switch (i){
                case 0:
                case 7:
                    board[0][i] = new ChessPiece(WHITE, ROOK);
                    board[7][i] = new ChessPiece(BLACK, ROOK);
                    break;
                case 1:
                case 6:
                    board[0][i] = new ChessPiece(WHITE, KNIGHT);
                    board[7][i] = new ChessPiece(BLACK, KNIGHT);
                    break;
                case 2:
                case 5:
                    board[0][i] = new ChessPiece(WHITE, BISHOP);
                    board[7][i] = new ChessPiece(BLACK, BISHOP);
                    break;
                case 3:
                    board[0][i] = new ChessPiece(WHITE, QUEEN);
                    board[7][i] = new ChessPiece(BLACK, QUEEN);
                    break;
                case 4:
                    board[0][i] = new ChessPiece(WHITE, KING);
                    board[7][i] = new ChessPiece(BLACK, KING);
                    break;
            }
        }
        // Sets Pawns
        for(int i = 0; i < board[0].length; i++){
            board[1][i] = new ChessPiece(WHITE, PAWN);
            board[6][i] = new ChessPiece(BLACK, PAWN);
        }
    }
}
