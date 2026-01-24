package chess;

import java.util.ArrayList;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

public class PieceMovesCalculator {
    private static ChessBoard board = new ChessBoard();
    private static ChessPosition myPosition = null;
    private static ChessPiece piece;
    private static boolean notMoved = true;
    int pieceRow;
    int pieceCol;
    List<ChessMove> allPossibleMoves = new ArrayList<>();
    ChessGame.TeamColor teamColor;

    PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        pieceRow = myPosition.getRow();
        pieceCol = myPosition.getColumn();
        this.board = board;
        this.myPosition = myPosition;
        piece = board.getPiece(myPosition);
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
            int colorChecker = 0;

            // Ensures pawns move and check captures the correct direction
            if(teamColor == ChessGame.TeamColor.WHITE){
                colorChecker = 1;
            }else{
                colorChecker = -1;
            }
            if(((pieceRow + colorChecker) == 1) || ((pieceRow + colorChecker) == 8)){
                // If one move would promote
                if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol)) == null) {
                    // If direct movement is unblocked, add promotions for pushing
                    promotionMoves(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol));
                }
                if(pieceCol == 1){
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)) != null) {
                        // If on left side of board don't check left captures
                        if (board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)).getTeamColor() != teamColor) {
                            // If piece in capture spot isn't team color add all promotions from capture
                            promotionMoves(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol + 1));
                        }
                    }
                }else if(pieceCol == 8){
                    // If on right side of board, don't check right captures
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)) != null) {
                        if (board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)).getTeamColor() != teamColor) {
                            // If piece in capture spot isn't team color add all promotions from capture
                            promotionMoves(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol - 1));
                        }
                    }
                }else{
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)) != null){
                        if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)).getTeamColor() != teamColor){
                            // If piece in capture spot isn't team color add all promotions from capture
                            promotionMoves(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol + 1));
                        }
                    }
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)) != null) {
                        if (board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)).getTeamColor() != teamColor) {
                            // If piece in capture spot isn't team color add all promotions from capture
                            promotionMoves(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol - 1));
                        }
                    }

                }
            }else if((pieceRow == 1 && teamColor == BLACK) || (pieceRow == 8 && teamColor == WHITE)){

            }
            else{
                // Handle all other captures that don't promote
                if(pieceCol == 1 && board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)) != null){
                    // If on left side of board don't check left captures
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)).getTeamColor() != teamColor){
                        // If piece in capture spot isn't team color add all promotions from capture
                        allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol + 1), null));
                    }
                }else if(pieceCol == 8 && board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)) != null) {
                    // If on right side of board, don't check right captures
                    if (board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)).getTeamColor() != teamColor) {
                        // If piece in capture spot isn't team color add all promotions from capture
                        allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol - 1), null));
                    }
                }else if (pieceCol != 1 && pieceCol !=8){
                    // Not on edge of board and not promoting
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)) != null) {
                        if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)).getTeamColor() != teamColor) {
                            // If piece in capture spot isn't team color add capture
                            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol + 1), null));
                        }
                    }
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)) != null) {
                        if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)).getTeamColor() != teamColor) {
                            // If piece in capture spot isn't team color add all promotions from capture
                            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol - 1), null));
                        }
                    }
                }
                // Add basic move
                if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol)) == null) {
                    allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol), null));
                }
                // Adds double move
                if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol)) == null){
                    if(((pieceRow == 7 && teamColor == BLACK) || (pieceRow == 2 && teamColor == WHITE)) && notMoved){
                        // Check for double movement
                        if(board.getPiece(new ChessPosition(pieceRow + colorChecker + colorChecker, pieceCol)) == null) {
                            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + colorChecker + colorChecker, pieceCol), null));
                        }
                    }
                }
            }
        }
    }
}
