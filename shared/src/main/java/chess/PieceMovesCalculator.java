package chess;
import chess.ChessPiece.*;

import java.util.ArrayList;
import java.util.List;

public class PieceMovesCalculator{
private final ChessBoard board;
private final ChessPosition myPosition;
private ChessPiece piece;
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

    switch(piece.getPieceType()){
        case PAWN:
            // PawnMoves allMoves = new PawnMoves(board, myPosition);
        case KNIGHT:
            // KnightMoves allMoves = new KnightMoves(board, myPosition);
        case BISHOP:
            // BishopMoves allMoves = new BishopMoves(board, myPosition);
        case ROOK:
            // RookMoves allMoves = new RookMoves(board, myPosition);
        case QUEEN:
            // QueenMoves allMoves = new QueenMoves(board, myPosition);
        case KING:
            KingMoves allMoves = new KingMoves(board, myPosition);
    }
}

public List<ChessMove> getAllPossibleMoves() {
    return allPossibleMoves;
}

public class KingMoves extends PieceMovesCalculator {

    KingMoves(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        kingMoves();
    }

    private void kingMoves() {
        if(pieceRow != 8) {
            // Not at top, add all forward moves
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + 1, pieceCol), piece.getPieceType()));
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + 1, pieceCol + 1), piece.getPieceType()));
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + 1, pieceCol - 1), piece.getPieceType()));
        }
        if(pieceRow != 1) {
            // Not at bottom, add all backwards moves
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow - 1, pieceCol), piece.getPieceType()));
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow - 1, pieceCol + 1), piece.getPieceType()));
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow - 1, pieceCol - 1), piece.getPieceType()));
        }
        if(pieceCol != 1) {
            // Not on far left, add all leftward moves
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + 1, pieceCol - 1), piece.getPieceType()));
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow, pieceCol - 1), piece.getPieceType()));
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow - 1, pieceCol - 1), piece.getPieceType()));
        }
        if(pieceCol != 8) {
            // Not on far right, add all rightward moves
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow + 1, pieceCol + 1), piece.getPieceType()));
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow, pieceCol + 1), piece.getPieceType()));
            allPossibleMoves.add(new ChessMove(myPosition, new ChessPosition(pieceRow - 1, pieceCol + 1), piece.getPieceType()));
        }
    }

    private void diagonalMoves(ChessBoard board, ChessPosition myPosition){
        // Logic for moving diagonally

        // Until you reach the top rank
        for(int newRow = pieceRow; newRow < 9; newRow++){
            // Right Diagonal Up
            for(int newCol = pieceCol; newCol < 9; newCol++){
                // Until you reach the far right rank
                ChessPosition possibleMove = new ChessPosition(newRow, newCol);
                if(board.getPiece(possibleMove).getPieceType() != null){
                    // If you encounter a piece, stop looking that direction and add capture if needed
                    if(board.getPiece(possibleMove).getTeamColor() != teamColor){
                        allPossibleMoves.add(new ChessMove(myPosition, possibleMove, piece.getPieceType()));
                    }
                    break;
                }
            }

            // Left Diagonal Up
            for(int newCol = pieceCol; newCol > 0; newCol--){
                // Until you reach the far left rank
                ChessPosition possibleMove = new ChessPosition(newRow, newCol);
                if(board.getPiece(possibleMove).getPieceType() != null){
                    // If you encounter a piece, stop looking that direction and add capture if needed
                    if(board.getPiece(possibleMove).getTeamColor() != teamColor){
                        allPossibleMoves.add(new ChessMove(myPosition, possibleMove, piece.getPieceType()));
                    }
                    break;
                }
            }
        }

        // Until you reach the bottom rank
        for(int newRow = pieceRow; newRow > 0; newRow--){
            // Left Diagonal Down
            for(int newCol = pieceCol; newCol > 0; newCol--){
                // Until you reach the far left rank
                ChessPosition possibleMove = new ChessPosition(newRow, newCol);
                if(board.getPiece(possibleMove).getPieceType() != null){
                    // If you encounter a piece, stop looking that direction and add capture if needed
                    if(board.getPiece(possibleMove).getTeamColor() != teamColor){
                        allPossibleMoves.add(new ChessMove(myPosition, possibleMove, piece.getPieceType()));
                    }
                    break;
                }
            }

            // Right Diagonal Down
            for(int newCol = pieceCol; newCol < 9; newCol++){
                // Until you reach the far right rank
                ChessPosition possibleMove = new ChessPosition(newRow, newCol);
                if(board.getPiece(possibleMove).getPieceType() != null){
                    // If you encounter a piece, stop looking that direction and add capture if needed
                    if(board.getPiece(possibleMove).getTeamColor() != teamColor){
                        allPossibleMoves.add(new ChessMove(myPosition, possibleMove, piece.getPieceType()));
                    }
                    break;
                }
            }
        }
    }
}

    public class BishopMoves extends PieceMovesCalculator {

        BishopMoves(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
            bishopMoves();
        }

        private void bishopMoves() {

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
