package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
    private static final String HIGHLIGHT_LIGHT = SET_BG_COLOR_BLUE;
    private static final String HIGHLIGHT_DARK = SET_BG_COLOR_GREEN;
    private static Collection<ChessPosition> allHighlightSquares;
    private static ChessPosition highlightPiece;
    public static void highlight(ChessGame game, TeamColor printPerspective, ChessPosition highlightSquare){
        // Prints a chess board with the valid moves of a piece highlighted
        highlightPiece = highlightSquare;
        allHighlightSquares = new ArrayList<>();
        Collection<ChessMove> allPossibleMoves = game.validMoves(highlightSquare);
        if(allPossibleMoves != null) {
            for (ChessMove move : allPossibleMoves) {
                allHighlightSquares.add(move.getEndPosition());
            }
        }
        print(game, printPerspective, allHighlightSquares);
    }

    public static void print(ChessGame game, TeamColor printPerspective, Collection<ChessPosition> highlightSquares) {
        allHighlightSquares = highlightSquares;
        // Prints out the given chess board from the given perspective
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
                    out.print(highlightCheck(rowNum, boardCol, SET_BG_COLOR_LIGHT_GREY));
                }else{
                    out.print(highlightCheck(rowNum, boardCol, SET_BG_COLOR_DARK_GREEN));
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

    private static String highlightCheck(int row, int col, String color){
        if(allHighlightSquares == null){
            return color;
        }
        int tempCorrector = 9;
        if(colorCorrector == 7){
            tempCorrector = 0;
        }
        if(allHighlightSquares.contains(new ChessPosition(Math.abs((row + 1) - tempCorrector), col + 1))){
            if(Objects.equals(color, SET_BG_COLOR_LIGHT_GREY)){
                return HIGHLIGHT_LIGHT;
            }else{
                return HIGHLIGHT_DARK;
            }
        }
        if(new ChessPosition(Math.abs((row + 1) - tempCorrector), col + 1).equals(highlightPiece)){
            return SET_BG_COLOR_RED;
        }
        return color;
    }

}