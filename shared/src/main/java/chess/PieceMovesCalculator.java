package chess;
import chess.ChessPiece.*;

import java.util.ArrayList;
import java.util.List;

import static chess.ChessPiece.PieceType.*;

public class PieceMovesCalculator{
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private ChessPiece piece;
    private boolean hasMoved = false;
    PieceMovesCalculator(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
        piece = board.getPiece(myPosition);
        switch(piece.getPieceType()){
            case PAWN:
            case KNIGHT:
            case BISHOP:
            case ROOK:
            case QUEEN:
            case KING:
        }
            
    }


    private List<ChessMove> pawnMoves() {
        List<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        // ChessMove needs startPosition, endPosition, and promotionPiece
        
        ChessPosition advanceTest = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
        ChessPosition doubleAdvanceTest = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());

        if(board.getPiece(advanceTest) == null && advanceTest.getRow() == 8){
            // IF pawn can promote, add promotions
            ChessMove knightPromotion = new ChessMove(myPosition, advanceTest, KNIGHT);
            ChessMove bishopPromotion = new ChessMove(myPosition, advanceTest, BISHOP);
            ChessMove rookPromotion = new ChessMove(myPosition, advanceTest, ROOK);
            ChessMove queenPromotion = new ChessMove(myPosition, advanceTest, QUEEN);
            possibleMoves.add(knightPromotion);
            possibleMoves.add(bishopPromotion);
            possibleMoves.add(rookPromotion);
            possibleMoves.add(queenPromotion);
        } else if(board.getPiece(advanceTest) == null){
            // IF pawn can move forward and will not promote
            if(!hasMoved && board.getPiece(doubleAdvanceTest) == null){
                // IF pawn hasn't moved yet and nothing blocking it, can move twice as far
                ChessMove doubleAdvance = new ChessMove(myPosition, doubleAdvanceTest, null);
                possibleMoves.add(doubleAdvance);
            }
            ChessMove advanceMove = new ChessMove(myPosition, advanceTest, null);
            possibleMoves.add(advanceMove);
        }

        // Ensure checks to capture don't go out of bounds of array. Also, can capture and promote
        // If can capture diagonally, Must be separate if/else chain
        // Check if en passant
        return possibleMoves;
    }
}
