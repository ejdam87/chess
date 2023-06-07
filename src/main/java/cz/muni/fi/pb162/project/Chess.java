package cz.muni.fi.pb162.project;

import cz.muni.fi.pb162.project.exceptions.MissingPlayerException;
import cz.muni.fi.pb162.project.strategies.MoveStrategy;
import cz.muni.fi.pb162.project.utils.BoardNotation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Class representing a game of chess
 *
 * @author Adam Dzadon
 */
public class Chess extends Game implements GameWritable {

    private Chess(Player player1, Player player2, Board board, MoveStrategy move1, MoveStrategy move2) {
        super(player1, player2, board, move1, move2);
    }

    private Chess(Player player1, Player player2, Board board) {
        super(player1, player2, board);
    }

    /**
     * Creates new instance of chess object
     *
     * @param player1 - object representing player 1
     * @param player2 - object representing player 2
     */
    public Chess(Player player1, Player player2) {
        super(player1, player2);
    }

    /**
     * Helper method to write header into given writer
     *
     * @param writer where to write
     * @param p1     player 1
     * @param p2     player 2
     * @throws IOException when write fails
     */
    private void writeHeader(BufferedWriter writer, Player p1, Player p2) throws IOException {
        writer.write(p1.name() + "-" + p1.color().toString());
        writer.write(';');
        writer.write(p2.name() + "-" + p2.color().toString());
    }

    /**
     * Helper method to write given board to given writer
     *
     * @param writer where to write
     * @param board  board to write
     * @throws IOException when write fails
     */
    private void writeBoard(BufferedWriter writer, Board board) throws IOException {

        String[] stringRow = new String[8];

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Piece piece = board.getPiece(row, col);

                if (piece == null) {
                    stringRow[col] = "_";
                } else {
                    String color = piece.getColor().toString();
                    String type = piece.getPieceType().toString();
                    stringRow[col] = type + "," + color;
                }

            }

            writer.write(String.join(";", stringRow));
            writer.newLine();
        }
    }

    @Override
    public void write(OutputStream os) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        Board board = getBoard();
        Player p1 = getPlayerOne();
        Player p2 = getPlayerTwo();
        writeHeader(writer, p1, p2);
        writeBoard(writer, board);
        writer.flush();
    }

    @Override
    public void write(File file) throws IOException {
        try (OutputStream os = new FileOutputStream(file)) {
            write(os);
        }
    }

    /**
     * Nested class for building a new chess instance (able to use sequential calls)
     *
     * @author Adam Dzadon
     */
    public static class Builder implements Buildable<Chess>, GameReadable {

        private Player player1;
        private Player player2;
        private MoveStrategy move1;
        private MoveStrategy move2;
        private final Board board = new Board();

        /**
         * Adds next strategy
         *
         * @param strategy - strategy to be added
         * @return itself
         */
        public Builder addStrategy(MoveStrategy strategy) {
            if (move1 == null) {
                move1 = strategy;
            } else if (move2 == null) {
                move2 = strategy;
            }
            return this;
        }

        /**
         * Adds player for building chess game
         *
         * @param player player to add
         * @return instance of current builder
         */
        public Builder addPlayer(Player player) {

            if (player1 == null) {
                player1 = player;
            } else if (player2 == null) {
                player2 = player;
            }
            return this;
        }

        /**
         * Adds piece on given coordinates for building chess game
         *
         * @param piece        piece to put
         * @param letterNumber letter number of square where to put
         * @param number       number of square where to put
         * @return instance of current builder
         */
        public Builder addPieceToBoard(Piece piece, char letterNumber, int number) {
            Coordinates coordinates = BoardNotation.getCoordinatesOfNotation(letterNumber, number);
            board.putPieceOnBoard(coordinates.letterNumber(), coordinates.number(), piece);
            return this;
        }

        @Override
        public Chess build() {

            if (player1 == null || player2 == null) {
                throw new MissingPlayerException("Did not provide both players!");
            }

            if (move1 == null || move2 == null) {
                return new Chess(player1, player2, board);
            }

            return new Chess(player1, player2, board, move1, move2);
        }

        /**
         * Helper method to parse one line from input
         *
         * @param row  number of row
         * @param line string representation o line
         */
        private void parseLine(int row, String line) throws IOException {
            String[] pieceStrings = line.split(";");
            String[] pieceAttrs;
            int col = 0;
            for (String pieceString : pieceStrings) {

                // empty square
                if (pieceString.equals("_")) {
                    continue;
                }
                pieceAttrs = pieceString.split(",");

                if (pieceAttrs.length != 2) {
                    throw new IOException("Invalid format");
                }

                PieceType type;
                Color color;
                try {
                    type = PieceType.valueOf(pieceAttrs[0]);
                    color = Color.valueOf(pieceAttrs[1]);
                } catch (IllegalArgumentException ex) {
                    throw new IOException("Invalid data format", ex);
                }

                board.putPieceOnBoard(row, col, new Piece(color, type));
            }
        }

        @Override
        public Builder read(InputStream is) throws IOException {
            String line;
            int row = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                parseLine(row, line);
                row++;
            }
            return this;
        }

        /**
         * Helper which parses the header from given stream
         *
         * @param is given input stream
         * @throws IOException when IO error
         */
        private void parseHeader(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String header = reader.readLine();
            String[] playerStrings = header.split(";");

            if (playerStrings.length != 2) {
                throw new IOException("Invalid format");
            }

            for (String playerString : playerStrings) {
                String[] parts = playerString.split("-");

                if (parts.length != 2) {
                    throw new IOException("Invalid format");
                }

                Color color = Color.valueOf(parts[1]);
                String name = parts[0];
                Player p = new Player(name, color);
                addPlayer(p);
            }

        }

        @Override
        public Builder read(InputStream is, boolean hasHeader) throws IOException {
            if (hasHeader) {
                parseHeader(is);
            }
            return read(is);
        }

        @Override
        public Builder read(File file) throws IOException {
            return read(file, false);
        }

        @Override
        public Builder read(File file, boolean hasHeader) throws IOException {
            try (InputStream is = new FileInputStream(file)) {
                return read(is, hasHeader);
            }
        }
    }

    /**
     * Private method to set up a row full of pawns of given color
     *
     * @param row   - row to be set
     * @param color - color of pawns
     */
    private void setPawns(int row, Color color) {
        Piece pawn = new Piece(color, PieceType.PAWN);
        for (int col = 0; col < Board.SIZE; col++) {
            putPieceOnBoard(row, col, pawn.makeClone());
        }
    }

    /**
     * Private method to set the "first" row of pieces ( rooks, bishops, king etc. )
     *
     * @param row   - row to be set
     * @param color - color of pieces
     */
    private void setPieces(int row, Color color) {
        Piece[] pieces = {
                new Piece(color, PieceType.ROOK),
                new Piece(color, PieceType.KNIGHT),
                new Piece(color, PieceType.BISHOP),
                new Piece(color, PieceType.QUEEN),
                new Piece(color, PieceType.KING),
                new Piece(color, PieceType.BISHOP),
                new Piece(color, PieceType.KNIGHT),
                new Piece(color, PieceType.ROOK),
        };

        for (int col = 0; col < Board.SIZE; col++) {
            putPieceOnBoard(row, col, pieces[col]);
        }
    }

    @Override
    public void setInitialSet() {
        setPawns(1, Color.BLACK);
        setPieces(0, Color.BLACK);

        setPawns(Board.SIZE - 2, Color.WHITE);
        setPieces(Board.SIZE - 1, Color.WHITE);
    }

    /**
     * Helper method to get the king of given player
     *
     * @param player - player to get the king of
     * @return king piece of given player
     */
    private Piece getKingOf(Player player) {
        Piece[] currentPieces = getBoard().getAllByColor(player.color());
        for (Piece piece : currentPieces) {
            if (piece.getPieceType() == PieceType.KING) {
                return piece;
            }
        }
        return null;
    }

    @Override
    public boolean isCheckOf(Player player) {
        Player opposing = getPlayerOne().equals(player) ? getPlayerTwo() : getPlayerOne();
        Piece currentKing = getKingOf(player);

        assert currentKing != null;
        Coordinates kingCoordinates = getBoard().findCoordinatesOfPieceById(currentKing.getId());
        return movesByPlayerUnrestricted(opposing).contains(kingCoordinates);
    }

    /**
     * Checks whether mate occurred
     *
     * @return true if mate occurred
     */
    private boolean isMate() {
        return movesByPlayer(getCurrentPlayer()).isEmpty() && isCheck();
    }

    /**
     * Checks if any king is endangered
     *
     * @return true if either king is endangered
     */
    private boolean isCheck() {
        return isCheckOf(getCurrentPlayer()) || isCheckOf(getOpposingPlayer());
    }

    /**
     * Checks whether "pat" occurred
     *
     * @return true if pat occurred
     */
    private boolean isPat() {
        return movesByPlayer(getCurrentPlayer()).isEmpty() && !isCheck();
    }

    @Override
    public void updateStatus() {
        if (isMate()) {
            setStateOfGame(StateOfGame.MATE);
        } else if (isCheck()) {
            setStateOfGame(StateOfGame.CHECK);
        } else if (isPat()) {
            setStateOfGame(StateOfGame.PAT);
        } else {
            setStateOfGame(StateOfGame.PLAYING);
        }
    }

    /**
     * Private method to promote piece at the end of board ( if possible ) else does nothing
     *
     * @param coords - coords of moved piece
     */
    private void promotion(Coordinates coords) {

        Piece currentPiece = getBoard().getPiece(coords);
        if (currentPiece == null) {
            return;
        }

        if (currentPiece.getPieceType() == PieceType.PAWN) {
            if (coords.letterNumber() == 0 || coords.letterNumber() == Board.SIZE - 1) {
                Color currentColor = currentPiece.getColor();
                Piece queen = new Piece(currentColor, PieceType.QUEEN);
                putPieceOnBoard(coords.letterNumber(), coords.number(), queen);
            }
        }
    }

    @Override
    public void move(Coordinates coords1, Coordinates coords2) {
        super.move(coords1, coords2);
        promotion(coords2);
    }
}
