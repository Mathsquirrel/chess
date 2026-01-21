package chess;
import chess.ChessPiece.*;

import java.util.ArrayList;
import java.util.List;

public class PieceMovesCalculator {
    private static ChessBoard board = new ChessBoard();
    private static ChessPosition myPosition = null;
    private static ChessPiece piece;
    private boolean hasMoved = false;
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

public void orthogonalMoves(ChessBoard board, ChessPosition myPosition) {

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
    while(testingRow < 8 && testingCol < 8){
        // Test diagonal up right movement
        ChessPosition testingPosition = new ChessPosition(testingRow + 1, testingCol + 1);
        if(board.getPiece(testingPosition) == null){
            // While squares are empty, add possible movement
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol + 1), null));
            testingRow++;
            testingCol++;
        }else if(board.getPiece(testingPosition).getTeamColor() != teamColor){
            // If enemy blocking, add move but break from loop
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol + 1), null));
            break;
        }else{
            // Friendly piece blocking, break
            break;
        }
    }

    testingRow = pieceRow;
    testingCol = pieceCol;
    // Logic for Diagonal Up left
    while(testingRow < 8 && testingCol > 1){
        // Test diagonal up right movement
        ChessPosition testingPosition = new ChessPosition(testingRow + 1, testingCol - 1);
        if(board.getPiece(testingPosition) == null){
            // While squares are empty, add possible movement
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol - 1), null));
            testingRow++;
            testingCol--;
        }else if(board.getPiece(testingPosition).getTeamColor() != teamColor){
            // If enemy blocking, add move but break from loop
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow + 1, testingCol - 1), null));
            break;
        }else{
            // Friendly piece blocking, break
            break;
        }
    }

    testingRow = pieceRow;
    testingCol = pieceCol;
    // Logic for Diagonal Down Right
    while(testingRow > 1 && testingCol < 8){
        // Test diagonal up right movement
        ChessPosition testingPosition = new ChessPosition(testingRow - 1, testingCol + 1);
        if(board.getPiece(testingPosition) == null){
            // While squares are empty, add possible movement
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol + 1), null));
            testingRow--;
            testingCol++;
        }else if(board.getPiece(testingPosition).getTeamColor() != teamColor){
            // If enemy blocking, add move but break from loop
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol + 1), null));
            break;
        }else{
            // Friendly piece blocking, break
            break;
        }
    }

    // Logic for Diagonal Down Left
    testingRow = pieceRow;
    testingCol = pieceCol;
    while(testingRow > 1 && testingCol > 1){
        // Test diagonal up right movement
        ChessPosition testingPosition = new ChessPosition(testingRow - 1, testingCol - 1);
        if(board.getPiece(testingPosition) == null){
            // While squares are empty, add possible movement
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol - 1), null));
            testingRow--;
            testingCol--;
        }else if(board.getPiece(testingPosition).getTeamColor() != teamColor){
            // If enemy blocking, add move but break from loop
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(testingRow - 1, testingCol - 1), null));
            break;
        }else{
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


/**
   REMEMBER THAT PAWN LOGIC CHANGES BASED ON COLOR. CHECK COLOR AND THEN ASSIGN VARIABLE TO NUMBER BASED ON COLOR AND APPLY THAT VARIABLE
   TO ALL CHECKS. Ex: White = 1, Black = -1. finalPosition = currentPosition(x + COLOR, COL) so that check logic doesn't care about color

   private class PawnMoves extends PieceMovesCalculator {

    PawnMoves(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    private void promotionMoves(ChessPosition currentPosition, ChessPosition finalPosition){
        ChessMove knightPromotion = new ChessMove(myPosition, finalPosition, KNIGHT);
        ChessMove bishopPromotion = new ChessMove(myPosition, finalPosition, BISHOP);
        ChessMove rookPromotion = new ChessMove(myPosition, finalPosition, ROOK);
        ChessMove queenPromotion = new ChessMove(myPosition, finalPosition, QUEEN);
        allPossibleMoves.add(knightPromotion);
        allPossibleMoves.add(bishopPromotion);
        allPossibleMoves.add(rookPromotion);
        allPossibleMoves.add(queenPromotion);
    }

    public List<ChessMove> main(){
        int pieceRow = myPosition.getRow();
        int pieceCol = myPosition.getColumn();
        // ChessMove needs startPosition, endPosition, and promotionPiece

        ChessPosition advanceTest = new ChessPosition(pieceRow+1, pieceCol);
        ChessPosition doubleAdvanceTest = new ChessPosition(pieceRow+2, pieceCol);

        if(board.getPiece(advanceTest) == null && advanceTest.getRow() == 8){
            // IF pawn can promote, add promotions
            promotionMoves(myPosition, advanceTest);
        } else if(board.getPiece(advanceTest) == null){
            // IF pawn can move forward and will not promote
            if(!hasMoved && board.getPiece(doubleAdvanceTest) == null){
                // IF pawn hasn't moved yet and nothing blocking it, can move twice as far
                ChessMove doubleAdvance = new ChessMove(myPosition, doubleAdvanceTest, null);
                allPossibleMoves.add(doubleAdvance);
            }
            ChessMove advanceMove = new ChessMove(myPosition, advanceTest, null);
            allPossibleMoves.add(advanceMove);
        }

        if(pieceCol == 1){
            // IF pawn is on first col, check only for right captures
        }else if(pieceCol == 8){
            // IF pawn is on last col, check only for left capture
        }else{
            // Pawn is in location to check for both
        }
        // Ensure checks to capture don't go out of bounds of array. Also, can capture and promote
        // If it can capture diagonally, Must be separate if/else chain
        // Check if en passant
        return allPossibleMoves;
    }*/
}
