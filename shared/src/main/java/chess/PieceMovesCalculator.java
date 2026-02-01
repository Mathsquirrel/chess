package chess;

import java.util.ArrayList;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

public class PieceMovesCalculator {
    private static ChessBoard board = new ChessBoard();
    private static ChessPosition myPosition = null;
    int pieceRow;
    int pieceCol;
    List<ChessMove> allPossibleMoves = new ArrayList<>();
    ChessGame.TeamColor teamColor;

    PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        pieceRow = myPosition.getRow();
        pieceCol = myPosition.getColumn();
        PieceMovesCalculator.board = board;
        PieceMovesCalculator.myPosition = myPosition;
        ChessPiece piece = board.getPiece(myPosition);
        teamColor = piece.getTeamColor();
    }

    public List<ChessMove> getAllPossibleMoves() {
        return allPossibleMoves;
    }

    public static class KingMoves extends PieceMovesCalculator {

        KingMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            kingMoves();
        }

        private void kingMoves() {
            // All possible variations of movement
            int[] allRowVariations = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] allColVariations = {1, 0, -1, 1, -1, 1, 0, -1};
            validMoves(board, myPosition, allRowVariations, allColVariations, false);
        }
    }

    public static class KnightMoves extends PieceMovesCalculator {

        KnightMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            knightMoves();
        }

        private void knightMoves() {
            // All possible variations of movement
            int[] allRowVariations = {2, 2, 1, 1, -1, -1, -2, -2};
            int[] allColVariations = {-1, 1, -2, 2, -2, 2, -1, 1};
            validMoves(board, myPosition, allRowVariations, allColVariations, false);
        }
    }

    public void diagonalMoves(ChessBoard board, ChessPosition myPosition) {
        int [] verticalMovements = {1, 1, -1, -1};
        int [] horizontalMovements = {1, -1, 1, -1};
        validMoves(board, myPosition, verticalMovements, horizontalMovements, true);
    }

    public void orthogonalMoves(ChessBoard board, ChessPosition myPosition) {
        int [] verticalMovements = {1, -1, 0, 0};
        int [] horizontalMovements = {0, 0, 1, -1};
        validMoves(board, myPosition, verticalMovements, horizontalMovements, true);
    }

    public static class QueenMoves extends PieceMovesCalculator {

        QueenMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            orthogonalMoves(board, myPosition);
            diagonalMoves(board, myPosition);
        }
    }

    public static class RookMoves extends PieceMovesCalculator {

        RookMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            orthogonalMoves(board, myPosition);
        }
    }

    public static class BishopMoves extends PieceMovesCalculator {

        BishopMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            diagonalMoves(board, myPosition);
        }
    }

    public void validMoves(ChessBoard board, ChessPosition myPosition, int[] verticalMoves, int [] horizontalMoves, boolean continuous){
        int testingRow;
        int testingCol;

        if(continuous) {
            // Check until hit end of board or other piece
            for (int i = 0; i < verticalMoves.length; i++) {
                testingRow = pieceRow;
                testingCol = pieceCol;
                testingRow += verticalMoves[i];
                testingCol += horizontalMoves[i];
                while (testingRow <= 8 && testingRow > 0 && testingCol <= 8 && testingCol > 0) {
                    ChessPosition testingPosition = new ChessPosition(testingRow, testingCol);
                    if (board.getPiece(testingPosition) == null) {
                        // While squares are empty, add possible movement
                        allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow, testingCol), null));
                        testingRow += verticalMoves[i];
                        testingCol += horizontalMoves[i];
                    } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                        // If enemy blocking, add move but break from loop
                        allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow, testingCol), null));
                        break;
                    } else {
                        // Friendly piece blocking, break
                        break;
                    }
                }
            }
        }
        else{
            // Check single movements
            for (int i = 0; i < verticalMoves.length; i++) {
                testingRow = pieceRow;
                testingCol = pieceCol;
                testingRow += verticalMoves[i];
                testingCol += horizontalMoves[i];
                if (testingRow <= 8 && testingRow > 0 && testingCol <= 8 && testingCol > 0) {
                    ChessPosition testingPosition = new ChessPosition(testingRow, testingCol);
                    if (board.getPiece(testingPosition) == null) {
                        // While squares are empty, add possible movement
                        allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow, testingCol), null));
                    } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                        // If enemy blocking, add move but break from loop
                        allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow, testingCol), null));
                    }
                }
            }
        }
    }

    public static class PawnMoves extends PieceMovesCalculator {

        PawnMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            pawnMoves();
        }

        private void promotionMoves(ChessPosition currentPosition, ChessPosition finalPosition) {
            ChessMove knightPromotion = new ChessMove(currentPosition, finalPosition, KNIGHT);
            ChessMove bishopPromotion = new ChessMove(currentPosition, finalPosition, BISHOP);
            ChessMove rookPromotion = new ChessMove(currentPosition, finalPosition, ROOK);
            ChessMove queenPromotion = new ChessMove(currentPosition, finalPosition, QUEEN);
            allPossibleMoves.add(knightPromotion);
            allPossibleMoves.add(bishopPromotion);
            allPossibleMoves.add(rookPromotion);
            allPossibleMoves.add(queenPromotion);
        }

        public void pawnMoves() {
            int pieceRow = myPosition.getRow();
            int pieceCol = myPosition.getColumn();
            int colorChecker = 1;
            int [] verticalMoves = {1, 1, 1};
            int [] horizontalMoves = {-1, 0, 1};
            // Ensures pawns move and check captures the correct direction
            if(teamColor == ChessGame.TeamColor.BLACK){
                verticalMoves = new int[]{-1, -1, -1};
                colorChecker = -1;
            }
            int testingRow;
            int testingCol;
            for(int i = 0; i < verticalMoves.length; i++){
                testingRow = pieceRow;
                testingCol = pieceCol;
                testingRow += verticalMoves[i];
                testingCol += horizontalMoves[i];

                if(testingRow <= 8 && testingRow > 0 && testingCol <= 8 && testingCol > 0) {
                    // If space checked is still within boundaries of board
                    ChessPosition testPosition = new ChessPosition(testingRow, testingCol);

                    if (horizontalMoves[i] == 0) {
                        // Regular push
                        if (board.getPiece(testPosition) == null) {
                            // If Promotion possible
                            if(testingRow == 1 || testingRow == 8){
                                promotionMoves(myPosition, testPosition);
                            } else {
                                // If on starting spot check for double push
                                if((pieceRow == 2 && teamColor.equals(WHITE)) || (pieceRow == 7 && teamColor.equals(BLACK))){
                                    ChessPosition doublePush = new ChessPosition(testingRow + colorChecker, testingCol);
                                    if(board.getPiece(doublePush) == null) {
                                        allPossibleMoves.add(new ChessMove(myPosition, doublePush, null));
                                    }
                                }
                                // Add regular push
                                allPossibleMoves.add(new ChessMove(myPosition, testPosition, null));
                            }
                        }
                    } else if(board.getPiece(testPosition) != null && board.getPiece(testPosition).getTeamColor() != teamColor){
                        // If movement is diagonal and can capture, add move
                        if(testingRow == 1 || testingRow == 8){
                            // Check for promotion vs regular capture
                            promotionMoves(myPosition, testPosition);
                        }else {
                            allPossibleMoves.add(new ChessMove(myPosition, testPosition, null));
                        }
                    }
                }
            }
        }
    }
}
