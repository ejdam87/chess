package cz.muni.fi.pb162.project;

/**
 * Class to represent a game of draughts between two players
 *
 * @author Adam Dzadon
 */
public class Draughts extends Game {

    /**
     * Creates new instnace of Draughts object
     *
     * @param player1 - representation of player 1
     * @param player2 - representation of player 2
     */
    public Draughts(Player player1, Player player2) {
        super(player1, player2);
    }

    @Override
    public void updateStatus() {
        Piece[] allPieces = getBoard().getAllPiecesFromBoard();
        boolean haveBlack = false;
        boolean haveWhite = false;

        for (Piece piece : allPieces) {
            if (piece.getColor() == Color.WHITE) {
                haveWhite = true;
            } else if (piece.getColor() == Color.BLACK) {
                haveBlack = true;
            }
        }

        if (haveWhite && !haveBlack) {
            setStateOfGame(StateOfGame.WHITE_PLAYER_WIN);
        }

        if (!haveWhite && haveBlack) {
            setStateOfGame(StateOfGame.BLACK_PLAYER_WIN);
        }

    }

    /**
     * Private method to set one side of initial set
     *
     * @param color - color of pieces
     */
    private void setSide(Color color) {

        int start;
        int step;
        int end;
        int doubleModulus; // decides on which rows to put two pieces based on color

        if (color == Color.WHITE) {
            start = 0;
            step = 1;
            end = 8;
            doubleModulus = 0;
        } else {
            start = 7;
            step = -1;
            end = -1;
            doubleModulus = 1;
        }

        Piece template = new Piece(color, PieceType.DRAUGHTS_MAN);

        for (int i = start; i != end; i += step) {
            if (i % 2 == doubleModulus) {
                putPieceOnBoard(i, start, template.makeClone());
                putPieceOnBoard(i, start + 2 * step, template.makeClone());
            } else {
                putPieceOnBoard(i, start + step, template.makeClone());
            }
        }
    }

    @Override
    public void setInitialSet() {
        setSide(Color.WHITE);
        setSide(Color.BLACK);
    }

    /**
     * Private method to perform draughts promotion of men
     *
     * @param toPromote - coordinate on which MAY be located piece to be promoted
     */
    private void promotion(Coordinates toPromote) {
        Piece piece = getBoard().getPiece(toPromote);
        if (piece.getColor() == Color.WHITE && toPromote.number() == 7) {
            Piece king = new Piece(Color.WHITE, PieceType.DRAUGHTS_KING);
            putPieceOnBoard(toPromote.letterNumber(), 7, king);
        }
        if (piece.getColor() == Color.BLACK && toPromote.number() == 0) {
            Piece king = new Piece(Color.BLACK, PieceType.DRAUGHTS_KING);
            putPieceOnBoard(toPromote.letterNumber(), 0, king);
        }
    }

    @Override
    public void move(Coordinates coords1, Coordinates coords2) {
        super.move(coords1, coords2);
        promotion(coords2);
    }
}
