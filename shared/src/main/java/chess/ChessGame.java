package chess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board = new ChessBoard();
    TeamColor currentTeam = TeamColor.WHITE;
    public ChessGame() {

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
            try {
                for(ChessMove move : allOptions){
                    ChessGame duplicateGame = (ChessGame) this.clone();
                    duplicateGame.setBoard(this.getBoard());
                    // For each move, if that move doesn't leave the king in check it's valid
                    duplicateGame.makeMove(move);
                    if(!duplicateGame.isInCheck(duplicateGame.getTeamTurn())){
                        validOptions.add(move);
                    }
                }
            }catch(CloneNotSupportedException e){
                e.printStackTrace();
            } catch (InvalidMoveException e) {
                throw new RuntimeException(e);
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
        if(validMoves(move.getStartPosition()).contains(move) && getTeamTurn() == getBoard().getPiece(move.getStartPosition()).getTeamColor()){
            // If the move is a valid move, and it's the turn of that piece
            ChessPiece piece = board.getPiece(move.getStartPosition());
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), piece);
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
                if(currentPiece.getPieceType() == ChessPiece.PieceType.KING && getTeamTurn() == teamColor){
                    kingPosition = new ChessPosition(i, j);
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
        outerloop:
        for(int i = 0; i < 8; i++){
            // For each row on the board
            for(int j = 0; j < 8; j++){
                ChessPiece currentPiece = board.getBoardState()[i][j];
                // For each piece on the board
                if(currentPiece.getTeamColor() != teamColor){
                    // If piece is opposite team, check moves of that piece
                    Collection<ChessMove> pieceMoves = currentPiece.pieceMoves(board, new ChessPosition(i, j));
                    for(ChessMove move : pieceMoves){
                        if(Objects.equals(move.getEndPosition(), getKingPosition(teamColor))){
                            inCheck = true;
                            break outerloop;
                        }
                    }
                }
            }
        }
        return inCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
