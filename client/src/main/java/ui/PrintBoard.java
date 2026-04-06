package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessGame.TeamColor;
import static ui.EscapeSequences.*;

public class PrintBoard {

    // Board dimensions.
    private static final String EMPTY_SMALL = "   ";
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;
    private static int colorCorrector;
    private static final String[] SMALL_HEADERS = { " 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 " };

    public static void highlight(ChessGame game, TeamColor printPerspective, ChessPosition highlightSquare){

    }

    public static void print(ChessGame game, TeamColor printPerspective) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        if(printPerspective == BLACK){
            colorCorrector = 7;
        }else{
            colorCorrector = 0;
        }
        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawChessBoard(out, game);

        drawHeaders(out);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawHeaders(PrintStream out) {
        setDarkGrey(out);

        String[] headers = { "\u2003a ", "\u2003b ", "\u2003c ", "\u2003d ", "\u2003e ", "\u2003f ", "\u2003g ", "\u2003h " };
        out.print(EMPTY_SMALL);
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[Math.abs(boardCol- colorCorrector)]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print(player);

        setDarkGrey(out);
    }

    private static void drawChessBoard(PrintStream out, ChessGame game) {
        out.print(SET_TEXT_COLOR_RED);
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            boolean firstSquareWhite = boardRow % 2 == 0;
            drawRowOfSquares(out, firstSquareWhite, game, boardRow);
        }
    }

    private static String pieceToPrint(ChessPiece checkedPiece){
        if(checkedPiece == null){
            return EMPTY;
        }
        boolean whitePiece = checkedPiece.getTeamColor() == WHITE;
        return switch (checkedPiece.getPieceType()) {
            case KING -> whitePiece ? BLACK_KING : WHITE_KING;
            case QUEEN -> whitePiece ? BLACK_QUEEN : WHITE_QUEEN;
            case ROOK -> whitePiece ? BLACK_ROOK : WHITE_ROOK;
            case BISHOP -> whitePiece ? BLACK_BISHOP : WHITE_BISHOP;
            case KNIGHT -> whitePiece ? BLACK_KNIGHT : WHITE_KNIGHT;
            case PAWN -> whitePiece ? BLACK_PAWN : WHITE_PAWN;
        };
    }

    private static void drawRowOfSquares(PrintStream out, boolean firstSquareWhite, ChessGame game, int rowNum) {
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if(boardCol == 0){
                    out.print(SET_BG_COLOR_DARK_GREY);
                    if(squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2){
                        out.print(SET_TEXT_COLOR_WHITE);
                        out.print(SMALL_HEADERS[Math.abs(colorCorrector -rowNum)]);
                    }else{
                        out.print(EMPTY_SMALL);
                    }
                }
                if(firstSquareWhite){
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                }else{
                    out.print(SET_BG_COLOR_DARK_GREEN);
                }
                firstSquareWhite = !firstSquareWhite;
                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    int tempCorrector = 0;
                    if(colorCorrector == 0){
                        tempCorrector = 7;
                    }
                    ChessPiece currentPiece = game.getBoard().getBoardState()[Math.abs(tempCorrector-rowNum)][Math.abs(colorCorrector -boardCol)];

                    // ALL I'm DOING IS MIRRORING THE BOARD. KING AND QUEEN ARE NOT ROTATING LIKE THEY SHOULD, THEY'RE MIRRORING
                    if(currentPiece == null) {

                    }else if(currentPiece.getTeamColor() == WHITE) {
                        out.print(SET_TEXT_COLOR_WHITE);
                    } else{
                        out.print(SET_TEXT_COLOR_BLACK);
                    }
                    out.print(pieceToPrint(currentPiece));
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }

                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    // Draw vertical column separator.
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                }

                if(boardCol == 7){
                    out.print(SET_BG_COLOR_DARK_GREY);
                    out.print(SET_TEXT_COLOR_WHITE);
                    if(squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2){
                        out.print(SMALL_HEADERS[Math.abs(colorCorrector - rowNum)]);
                    }else{
                        out.print(EMPTY_SMALL);
                    }
                }
                setDarkGrey(out);
            }

            out.println();
        }
    }

    private static void setDarkGrey(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

}