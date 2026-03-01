package chess;
import java.util.ArrayList;
import java.util.Collection;
import chess.ChessBoard.*;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board = new ChessBoard();
    TeamColor currentTeam;
    public ChessGame() {
        board.resetBoard();
        currentTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTeam == chessGame.currentTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTeam);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        } else {
            // Sets validOptions to all possible moves
            Collection<ChessMove> allOptions = board.getPiece(startPosition).pieceMoves(board, startPosition);
            Collection<ChessMove> validOptions = new ArrayList<>();
            ChessBoard duplicateBoard = new ChessBoard(board);
            for(ChessMove move : allOptions){
                // For each move, if that move doesn't leave the king in check it's valid
                ChessPiece piece = board.getPiece(move.getStartPosition());
                board.addPiece(move.getEndPosition(), piece);
                board.addPiece(move.getStartPosition(), null);
                if(!isInCheck(piece.getTeamColor())) {
                    validOptions.add(move);
                }
                board = new ChessBoard (duplicateBoard);
            }
            return validOptions;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(validMoves(move.getStartPosition()) != null && validMoves(move.getStartPosition()).contains(move) && getTeamTurn() == getBoard().getPiece(move.getStartPosition()).getTeamColor()){
            // If the move is a valid move, and it's the turn of that piece
            ChessPiece piece = board.getPiece(move.getStartPosition());
            board.addPiece(move.getStartPosition(), null);
            if(move.getPromotionPiece() != null){
                board.addPiece(move.getEndPosition(), new ChessPiece(getTeamTurn(), move.getPromotionPiece()));
            }else{
                board.addPiece(move.getEndPosition(), piece);
            }
            if(currentTeam == TeamColor.WHITE){
                setTeamTurn(TeamColor.BLACK);
            }else{
                setTeamTurn(TeamColor.WHITE);
            }
        }else{
            throw new InvalidMoveException("That was an invalid move");
        }
    }

    private ChessPosition getKingPosition(TeamColor teamColor){
        ChessPosition kingPosition = new ChessPosition(1, 1);
        outerloop:
        for(int i = 0; i < 8; i++){
            // For each row on the board
            for(int j = 0; j < 8; j++){
                ChessPiece currentPiece = board.getBoardState()[i][j];
                // For each piece on the board
                if(currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == teamColor){
                    kingPosition = new ChessPosition(i + 1, j + 1);
                    break outerloop;
                }
            }
        }
        return kingPosition;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean inCheck = false;
        ChessPosition kingPosition = getKingPosition(teamColor);
        outerloop:
        for (int i = 1; i < 9; i++) for (int j = 1; j < 9; j++){
            ChessPiece currentPiece = board.getBoardState()[i - 1][j - 1];
            // For each piece on the board

            if(currentPiece != null && currentPiece.getTeamColor() != teamColor){
                // If piece exists and is the opposite color

                Collection<ChessMove> pieceMoves = currentPiece.pieceMoves(board, new ChessPosition(i, j));
                for(ChessMove move : pieceMoves){
                    // For each move in the available moves, check if they can capture the king
                    if(Objects.equals(move.getEndPosition(), kingPosition)){
                        inCheck = true;
                        break outerloop;
                    }
                }
            }
        }
        return inCheck;
    }

    private Collection<ChessMove> allValidPieceMoves(TeamColor color){
        Collection<ChessMove> allValidPieceMoves = new ArrayList<>();
        for(int i = 1; i < 9; i++){
            // For each row on the board
            for(int j = 1; j < 9; j++){
                ChessPiece currentPiece = board.getBoardState()[i - 1][j - 1];
                // For each piece on the board
                if(currentPiece != null && currentPiece.getTeamColor() == color){
                    // Add all that pieces valid moves to the total available moves
                    allValidPieceMoves.addAll(validMoves(new ChessPosition(i, j)));
                }
            }
        }
        return allValidPieceMoves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            Collection<ChessMove> validMovements = allValidPieceMoves(teamColor);
            return validMovements.isEmpty();
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // Might need to add logic for 50 move rule
        Collection<ChessMove> validMovements = allValidPieceMoves(teamColor);

        // Must not be in check and must have no moves to return true
        return !isInCheck(teamColor) && (validMovements.isEmpty());
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
