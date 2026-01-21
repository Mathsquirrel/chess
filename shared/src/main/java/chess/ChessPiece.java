package chess;
import chess.PieceMovesCalculator.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{"+ pieceColor +
                ", " + type +
                '}';
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceType type = board.getPiece(myPosition).getPieceType();
        List<ChessMove> allMoves = new ArrayList<>();
        if(type == PieceType.KING){
            KingMoves allKingMoves = new KingMoves(board, myPosition);
            allMoves.addAll(allKingMoves.getAllPossibleMoves());
        }else if(type == PieceType.QUEEN){
            // QueenMoves allQueenMoves = new QueenMoves(board, myPosition);
            // allMoves.addAll(allQueenMoves.getAllPossibleMoves());
        }else if(type == PieceType.ROOK){
            // RookMoves allRookMoves = new RookMoves(board, myPosition);
            // allMoves.addAll(allRookMoves.getAllPossibleMoves());
        }else if(type == PieceType.BISHOP){
            BishopMoves allBishopMoves = new BishopMoves(board, myPosition);
            allMoves.addAll(allBishopMoves.getAllPossibleMoves());
        }else if(type == PieceType.KNIGHT){
            // KnightMoves allKnightMoves = new KnightMoves(board, myPosition);
            // allMoves.addAll(allKnightMoves.getAllPossibleMoves());
        }else if(type == PieceType.PAWN){
            // PawnMoves allPawnMoves = new PawnMoves(board, myPosition);
            // allMoves.addAll(allPawnMoves.getAllPossibleMoves());
        }
        return allMoves;
        // IF any of the movements in the array would have a piece move where there is a friendly piece, remove them
    }
}

