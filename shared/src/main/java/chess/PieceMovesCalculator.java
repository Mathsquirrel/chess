package chess;

import java.util.ArrayList;
import java.util.List;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;

public class PieceMovesCalculator {
    ChessGame.TeamColor teamColor;
    ChessPiece.PieceType piece;
    ChessBoard board;
    static ChessPosition myPosition;
    static List<ChessMove> allAvailableMoves;
    static int pieceRow;
    static int pieceCol;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
        teamColor = board.getPiece(myPosition).getTeamColor();
        piece = board.getPiece(myPosition).getPieceType();
        pieceRow = myPosition.getRow();
        pieceCol = myPosition.getColumn();
        allAvailableMoves = new ArrayList<>();
    }

    public List<ChessMove> getAvailableMoves(){
        return allAvailableMoves;
    }

    public static class KingMoves extends PieceMovesCalculator{
        public KingMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            int[] verticalMoves  = {1, 1, 1, 0, 0, -1, -1, -1};
            int[] horizontalMoves = {-1, 0, 1, -1, 1, -1, 0, 1};
            validMoves(board, myPosition, horizontalMoves, verticalMoves, false);
        }
    }

    public static class QueenMoves extends PieceMovesCalculator{
        public QueenMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            orthogonalMoves(board, myPosition);
            diagonalMoves(board, myPosition);
        }
    }

    public static class RookMoves extends PieceMovesCalculator{
        public RookMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            orthogonalMoves(board, myPosition);
        }
    }

    public static class BishopMoves extends PieceMovesCalculator{
        public BishopMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            diagonalMoves(board, myPosition);
        }
    }

    public static class KnightMoves extends PieceMovesCalculator{
        public KnightMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            int[] verticalMoves = {2, 2, 1, 1, -1, -1, -2, -2};
            int[] horizontalMoves = {-1, 1, -2, 2, -2, 2, -1, 1};
            validMoves(board, myPosition, horizontalMoves, verticalMoves, false);
        }
    }

    public static class PawnMoves extends PieceMovesCalculator{
        public PawnMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            int[] horizontalMoves = {-1, 0, 1};
            int[] verticalMoves = {1, 1, 1,};
            validMoves(board, myPosition, horizontalMoves, verticalMoves, false);
        }
    }

    public void orthogonalMoves(ChessBoard board, ChessPosition myPosition){
        int[] rookVerticalMoves = {1, -1, 0, 0};
        int[] rookHorizontalMoves = {0, 0, 1, -1};
        validMoves(board, myPosition, rookHorizontalMoves, rookVerticalMoves, true);
    }

    public void diagonalMoves(ChessBoard board, ChessPosition myPosition){
        int[] verticalMoves = {1, 1, -1, -1};
        int[] horizontalMoves = {1, -1, 1, -1};
        validMoves(board, myPosition, horizontalMoves, verticalMoves, true);
    }

    public void promotionMoves(ChessPosition myPosition, ChessPosition endPosition){
        ChessMove queenPromote = new ChessMove(myPosition, endPosition, QUEEN);
        ChessMove rookPromote = new ChessMove(myPosition, endPosition, ROOK);
        ChessMove bishopPromote = new ChessMove(myPosition, endPosition, BISHOP);
        ChessMove knightPromote = new ChessMove(myPosition, endPosition, KNIGHT);
        allAvailableMoves.add(queenPromote);
        allAvailableMoves.add(rookPromote);
        allAvailableMoves.add(bishopPromote);
        allAvailableMoves.add(knightPromote);
    }

    public void validMovesPawn(ChessBoard board, ChessPosition myPosition, int[] horizontalMovement, int[] verticalMovement, boolean isContinuous){
        int testRow;
        int testCol;
        // Handle Pawn movement
        int colorCorrector = 1;
        if(teamColor == BLACK){
            colorCorrector = -1;
        }

        for (int i = 0; i < horizontalMovement.length; i++) {
            testRow = pieceRow + verticalMovement[i]*colorCorrector;
            testCol = pieceCol + horizontalMovement[i];
            if (testRow <= 8 && testRow > 0 && testCol <= 8 && testCol > 0) {
                // While not out of bounds
                ChessPosition testPosition = new ChessPosition(testRow, testCol);
                ChessMove testMove = new ChessMove(myPosition, testPosition, null);
                if (horizontalMovement[i] == 0) {
                    // Check for regular push

                    // TOO DEEPLY NESTED
                    if(board.getPiece(testPosition) == null) {
                        if(testRow == 8 || testRow == 1){
                            promotionMoves(myPosition, testPosition);
                        }else {
                            allAvailableMoves.add(testMove);
                        }
                        ChessPosition doublePushPosition = new ChessPosition(testRow + colorCorrector, testCol);
                        if(((pieceRow == 2 && teamColor == WHITE) || (pieceRow == 7 && teamColor == BLACK)) && board.getPiece(doublePushPosition) == null){
                            // If pawn hasn't moved, check for double push
                            allAvailableMoves.add(new ChessMove(myPosition, doublePushPosition, null));
                        }
                    }
                }
                else if (board.getPiece(testPosition) != null && board.getPiece(testPosition).getTeamColor() != teamColor) {
                    // If piece can capture diagonally
                    if(testRow == 8 || testRow == 1) {
                        promotionMoves(myPosition, testPosition);
                    }else{
                        allAvailableMoves.add(testMove);
                    }
                }
            }
        }
    }

    public void validMoves(ChessBoard board, ChessPosition myPosition, int[] horizontalMovement, int[] verticalMovement, boolean isContinuous) {
        int testRow;
        int testCol;
        if (isContinuous) {
            // Handles continuous movement like bishop and rook
            for (int i = 0; i < horizontalMovement.length; i++) {
                testRow = pieceRow + verticalMovement[i];
                testCol = pieceCol + horizontalMovement[i];
                while (testRow <= 8 && testRow > 0 && testCol <= 8 && testCol > 0) {
                    // While not out of bounds
                    ChessPosition testPosition = new ChessPosition(testRow, testCol);
                    ChessMove testMove = new ChessMove(myPosition, testPosition, null);
                    if (board.getPiece(testPosition) == null) {
                        // If space is empty
                        allAvailableMoves.add(testMove);
                    } else if (board.getPiece(testPosition).getTeamColor() != teamColor) {
                        // If piece can be captured, add move but break movement
                        allAvailableMoves.add(testMove);
                        break;
                    } else {
                        // Friendly piece, break
                        break;
                    }
                    testRow += verticalMovement[i];
                    testCol += horizontalMovement[i];
                }
            }
        }else if(piece != PAWN){
            // Handles single movement for king and knight
            for (int i = 0; i < horizontalMovement.length; i++) {
                testRow = pieceRow + verticalMovement[i];
                testCol = pieceCol + horizontalMovement[i];
                if (testRow <= 8 && testRow > 0 && testCol <= 8 && testCol > 0) {
                    // While not out of bounds
                    ChessPosition testPosition = new ChessPosition(testRow, testCol);
                    ChessMove testMove = new ChessMove(myPosition, testPosition, null);
                    if (board.getPiece(testPosition) == null) {
                        // If space is empty
                        allAvailableMoves.add(testMove);
                    } else if (board.getPiece(testPosition).getTeamColor() != teamColor) {
                        // If piece can be captured, add move but break movement
                        allAvailableMoves.add(testMove);
                    }
                }
            }
        }else{
            validMovesPawn(board, myPosition, horizontalMovement, verticalMovement, false);
        }
    }
}
