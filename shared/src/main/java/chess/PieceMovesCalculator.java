package chess;
import chess.ChessPiece.*;

import java.util.ArrayList;
import java.util.List;

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
        teamColor = board.getPiece(myPosition).getTeamColor();
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
            for (int i = 0; i < allRowVariations.length; i++) {
                int possibleRow = myPosition.getRow() + allRowVariations[i];
                int possibleCol = myPosition.getColumn() + allColVariations[i];
                if (possibleRow < 1 || possibleRow > 8 || possibleCol < 1 || possibleCol > 8) {
                    // If the movement will go out of bounds, don't try it
                } else if (board.getPiece(new ChessPosition(possibleRow, possibleCol)) != null) {
                    if (board.getPiece(new ChessPosition(possibleRow, possibleCol)).getTeamColor() != teamColor) {
                        // If the movement does not end on a friendly piece's space, add as option
                        allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(possibleRow, possibleCol), null));
                    }
                } else {
                    allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(possibleRow, possibleCol), null));
                }
            }
        }
    }

    public static class KnightMoves extends PieceMovesCalculator {

        KnightMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            knightMoves();
        }

        private void knightMoves() {
            // All possible variations of movement
            // Same movement as king (8 squares) only modification is numbers in array
            // Create one function for both called king/knight and just pass in array or use different array based on value of piece
            int[] allRowVariations = {2, 2, 1, 1, -1, -1, -2, -2};
            int[] allColVariations = {-1, 1, -2, 2, -2, 2, -1, 1};
            for (int i = 0; i < allRowVariations.length; i++) {
                int possibleRow = myPosition.getRow() + allRowVariations[i];
                int possibleCol = myPosition.getColumn() + allColVariations[i];
                if (possibleRow < 1 || possibleRow > 8 || possibleCol < 1 || possibleCol > 8) {
                    // If the movement will go out of bounds, don't try it
                } else if (board.getPiece(new ChessPosition(possibleRow, possibleCol)) != null) {
                    if (board.getPiece(new ChessPosition(possibleRow, possibleCol)).getTeamColor() != teamColor) {
                        // If the movement does not end on a friendly piece's space, add as option
                        allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(possibleRow, possibleCol), null));
                    }
                } else {
                    allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(possibleRow, possibleCol), null));
                }
            }
        }
    }

    public void orthogonalMoves(ChessBoard board, ChessPosition myPosition) {
        // Make for loop that iterates through array like king movement and resets testing variables
        int testingRow = pieceRow;
        int testingCol = pieceCol;
        while (testingRow < 8) {
            // Test diagonal up right movement
            ChessPosition testingPosition = new ChessPosition(testingRow + 1, testingCol);
            if (board.getPiece(testingPosition) == null) {
                // While squares are empty, add possible movement
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol), null));
                testingRow++;
            } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                // If enemy blocking, add move but break from loop
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol), null));
                break;
            } else {
                // Friendly piece blocking, break
                break;
            }
        }
        testingRow = pieceRow;
        testingCol = pieceCol;
        while (testingRow > 1) {
            // Test diagonal up right movement
            ChessPosition testingPosition = new ChessPosition(testingRow - 1, testingCol);
            if (board.getPiece(testingPosition) == null) {
                // While squares are empty, add possible movement
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol), null));
                testingRow--;
            } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                // If enemy blocking, add move but break from loop
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol), null));
                break;
            } else {
                // Friendly piece blocking, break
                break;
            }
        }
        testingRow = pieceRow;
        testingCol = pieceCol;
        while (testingCol > 1) {
            // Test diagonal up right movement
            ChessPosition testingPosition = new ChessPosition(testingRow, testingCol - 1);
            if (board.getPiece(testingPosition) == null) {
                // While squares are empty, add possible movement
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow, testingCol - 1), null));
                testingCol--;
            } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                // If enemy blocking, add move but break from loop
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow, testingCol - 1), null));
                break;
            } else {
                // Friendly piece blocking, break
                break;
            }
        }
        testingRow = pieceRow;
        testingCol = pieceCol;
        while (testingCol < 8) {
            // Test diagonal up right movement
            ChessPosition testingPosition = new ChessPosition(testingRow, testingCol + 1);
            if (board.getPiece(testingPosition) == null) {
                // While squares are empty, add possible movement
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow, testingCol + 1), null));
                testingCol++;
            } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                // If enemy blocking, add move but break from loop
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow, testingCol + 1), null));
                break;
            } else {
                // Friendly piece blocking, break
                break;
            }
        }
    }

    public void diagonalMoves(ChessBoard board, ChessPosition myPosition) {
        // Logic for Diagonal up right
        int testingRow = pieceRow;
        int testingCol = pieceCol;
        while (testingRow < 8 && testingCol < 8) {
            // Test diagonal up right movement
            ChessPosition testingPosition = new ChessPosition(testingRow + 1, testingCol + 1);
            if (board.getPiece(testingPosition) == null) {
                // While squares are empty, add possible movement
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol + 1), null));
                testingRow++;
                testingCol++;
            } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                // If enemy blocking, add move but break from loop
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol + 1), null));
                break;
            } else {
                // Friendly piece blocking, break
                break;
            }
        }

        testingRow = pieceRow;
        testingCol = pieceCol;
        // Logic for Diagonal Up left
        while (testingRow < 8 && testingCol > 1) {
            // Test diagonal up right movement
            ChessPosition testingPosition = new ChessPosition(testingRow + 1, testingCol - 1);
            if (board.getPiece(testingPosition) == null) {
                // While squares are empty, add possible movement
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol - 1), null));
                testingRow++;
                testingCol--;
            } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                // If enemy blocking, add move but break from loop
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol - 1), null));
                break;
            } else {
                // Friendly piece blocking, break
                break;
            }
        }

        testingRow = pieceRow;
        testingCol = pieceCol;
        // Logic for Diagonal Down Right
        while (testingRow > 1 && testingCol < 8) {
            // Test diagonal up right movement
            ChessPosition testingPosition = new ChessPosition(testingRow - 1, testingCol + 1);
            if (board.getPiece(testingPosition) == null) {
                // While squares are empty, add possible movement
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol + 1), null));
                testingRow--;
                testingCol++;
            } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                // If enemy blocking, add move but break from loop
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol + 1), null));
                break;
            } else {
                // Friendly piece blocking, break
                break;
            }
        }

        // Logic for Diagonal Down Left
        testingRow = pieceRow;
        testingCol = pieceCol;
        while (testingRow > 1 && testingCol > 1) {
            // Test diagonal up right movement
            ChessPosition testingPosition = new ChessPosition(testingRow - 1, testingCol - 1);
            if (board.getPiece(testingPosition) == null) {
                // While squares are empty, add possible movement
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol - 1), null));
                testingRow--;
                testingCol--;
            } else if (board.getPiece(testingPosition).getTeamColor() != teamColor) {
                // If enemy blocking, add move but break from loop
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol - 1), null));
                break;
            } else {
                // Friendly piece blocking, break
                break;
            }
        }
    }

    public static class QueenMoves extends PieceMovesCalculator {

        QueenMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            queenMoves();
        }

        private void queenMoves() {
            orthogonalMoves(board, myPosition);
            diagonalMoves(board, myPosition);
        }
    }

    public static class RookMoves extends PieceMovesCalculator {

        RookMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            rookMoves();
        }

        private void rookMoves() {
            orthogonalMoves(board, myPosition);
        }
    }

    public static class BishopMoves extends PieceMovesCalculator {

        BishopMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            bishopMoves();
        }

        private void bishopMoves() {
            diagonalMoves(board, myPosition);
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
                if(pieceCol == 1 && board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)) != null){
                    // If on left side of board don't check left captures
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)).getTeamColor() != teamColor){
                        // If piece in capture spot isn't team color add all promotions from capture
                        promotionMoves(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol + 1));
                    }
                }else if(pieceCol == 8 && board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)) != null){
                    // If on right side of board, don't check right captures
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)).getTeamColor() != teamColor){
                        // If piece in capture spot isn't team color add all promotions from capture
                        promotionMoves(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol - 1));
                    }
                }else{
                    if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)) != null){
                        if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol + 1)).getTeamColor() != teamColor){
                            // If piece in capture spot isn't team color add all promotions from capture
                            promotionMoves(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol + 1));
                        }
                    }else if(board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)) != null) {
                        if (board.getPiece(new ChessPosition(pieceRow + colorChecker, pieceCol - 1)).getTeamColor() != teamColor) {
                            // If piece in capture spot isn't team color add all promotions from capture
                            promotionMoves(myPosition, new ChessPosition(pieceRow + colorChecker, pieceCol - 1));
                        }
                    }

                }
            }else{
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
                    if((pieceRow == 7 || pieceRow == 2) && notMoved && board.getPiece(new ChessPosition(pieceRow + colorChecker + colorChecker, pieceCol)) == null){
                        // Check for double movement
                        allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + colorChecker + colorChecker, pieceCol), null));
                    }
                }
            }
        }
    }
}
