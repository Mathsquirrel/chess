package chess;
import chess.ChessPiece.*;

import java.util.ArrayList;
import java.util.List;

public class PieceMovesCalculator{
private static ChessBoard board = new ChessBoard();
private static ChessPosition myPosition = null;
private static ChessPiece piece;
private boolean hasMoved = false;
int pieceRow;
int pieceCol;
List<ChessMove> allPossibleMoves = new ArrayList<>();
ChessGame.TeamColor teamColor;

    PieceMovesCalculator(ChessBoard board, ChessPosition myPosition){
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
        // IF move is not out of bounds and IF piece of same color already there, then can move

        // Put second if inside to check if spot is clear
        int[] allRowVariations = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] allColVariations = {1, 0, -1, 1, -1, 1, 0, -1};
        for (int i = 0; i < allRowVariations.length; i++) {
            int possibleRow = myPosition.getRow() + allRowVariations[i];
            int possibleCol = myPosition.getColumn() + allColVariations[i];
            if (possibleRow < 1 || possibleRow > 8 || possibleCol < 1 || possibleCol > 8) {

            } else if (board.getPiece(new ChessPosition(possibleRow, possibleCol)) != null) {
                if (board.getPiece(new ChessPosition(possibleRow, possibleCol)).getTeamColor() != teamColor) {
                    allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(possibleRow, possibleCol), null));
                }
            } else {
                allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(possibleRow, possibleCol), null));
            }
        }
    }
}

public void diagonalMoves(ChessBoard board, ChessPosition myPosition) {
    // Logic for moving diagonally

    // Until you reach the top rank
    for (int newRow = pieceRow; newRow < 8; newRow++) {
        // Right Diagonal Up
        for (int newCol = pieceCol; newCol < 8; newCol++) {
            // Until you reach the far right rank
            ChessPosition possibleMove = new ChessPosition(newRow, newCol);
            if (board.getPiece(possibleMove).getPieceType() != null) {
                // If you encounter a piece, stop looking that direction and add capture if needed
                if (board.getPiece(possibleMove).getTeamColor() != teamColor) {
                    allPossibleMoves.add(new ChessMove(myPosition, possibleMove, piece.getPieceType()));
                }
                break;
            }
        }

        // Left Diagonal Up
        for (int newCol = pieceCol; newCol > 0; newCol--) {
            // Until you reach the far left rank
            ChessPosition possibleMove = new ChessPosition(newRow, newCol);
            if (board.getPiece(possibleMove).getPieceType() != null) {
                // If you encounter a piece, stop looking that direction and add capture if needed
                if (board.getPiece(possibleMove).getTeamColor() != teamColor) {
                    allPossibleMoves.add(new ChessMove(myPosition, possibleMove, piece.getPieceType()));
                }
                break;
            }
        }
    }

    // Until you reach the bottom rank
    for (int newRow = pieceRow; newRow > 0; newRow--) {
        // Left Diagonal Down
        for (int newCol = pieceCol; newCol > 0; newCol--) {
            // Until you reach the far left rank
            ChessPosition possibleMove = new ChessPosition(newRow, newCol);
            if (board.getPiece(possibleMove).getPieceType() != null) {
                // If you encounter a piece, stop looking that direction and add capture if needed
                if (board.getPiece(possibleMove).getTeamColor() != teamColor) {
                    allPossibleMoves.add(new ChessMove(myPosition, possibleMove, piece.getPieceType()));
                }
                break;
            }
        }

        // Right Diagonal Down
        for (int newCol = pieceCol; newCol < 8; newCol++) {
            // Until you reach the far right rank
            ChessPosition possibleMove = new ChessPosition(newRow, newCol);
            if (board.getPiece(possibleMove).getPieceType() != null) {
                // If you encounter a piece, stop looking that direction and add capture if needed
                if (board.getPiece(possibleMove).getTeamColor() != teamColor) {
                    allPossibleMoves.add(new ChessMove(myPosition, possibleMove, piece.getPieceType()));
                }
                break;
            }
        }
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
