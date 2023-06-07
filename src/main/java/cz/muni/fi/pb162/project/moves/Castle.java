package cz.muni.fi.pb162.project.moves;

import cz.muni.fi.pb162.project.Board;
import cz.muni.fi.pb162.project.Color;
import cz.muni.fi.pb162.project.Coordinates;
import cz.muni.fi.pb162.project.Game;
import cz.muni.fi.pb162.project.Piece;
import cz.muni.fi.pb162.project.PieceType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Castle move
 *
 * @author Adam Dzadon
 */
public class Castle implements Move {

    /**
     * Helper method to check if there are empty places in given direction
     *
     * @param row  row of king
     * @param side side to move (1 for left, -1 for right)
     * @param game game instance
     * @return true if there are no pieces in given side
     */
    private boolean checkSides(int row, int side, Game game) {
        int i = 4 + side;
        while (i > 0 && i < Board.SIZE - 1) {
            if (!game.getBoard().isEmpty(row, i)) {
                return false;
            }
            i += side;
        }
        return true;
    }

    /**
     * Returns set of coordinates to which a king can move with "castle" move
     *
     * @param row   row of king (first or last based on color)
     * @param game  game instance
     * @param color color of king
     * @return set of coordinates to which the king of given color can move with "castle"
     */
    private Set<Coordinates> evalCastle(int row, Game game, Color color) {

        Set<Coordinates> res = new HashSet<>();

        if (checkSides(row, 1, game)) {
            Piece possibleRook = game.getBoard().getPiece(row, Board.SIZE - 1);
            if (possibleRook != null
                    && possibleRook.getPieceType() == PieceType.ROOK
                    && possibleRook.getColor() == color) {
                res.add(new Coordinates(row, Board.SIZE - 2));
            }
        }

        if (checkSides(row, -1, game)) {
            Piece possibleRook = game.getBoard().getPiece(row, 0);
            if (possibleRook != null
                    && possibleRook.getPieceType() == PieceType.ROOK
                    && possibleRook.getColor() == color) {
                res.add(new Coordinates(row, 1));
            }
        }

        return res;

    }

    @Override
    public Set<Coordinates> getAllowedMoves(Game game, Coordinates position) {
        Piece king = game.getBoard().getPiece(position);
        Set<Coordinates> res = new HashSet<>();

        if (king.getColor() == Color.BLACK) {
            if (!position.equals(new Coordinates(0, 4))) {
                return Collections.emptySet();
            }
            res.addAll(evalCastle(0, game, Color.BLACK));
        }

        if (king.getColor() == Color.WHITE) {
            if (!position.equals(new Coordinates(Board.SIZE - 1, 4))) {
                return Collections.emptySet();
            }
            res.addAll(evalCastle(Board.SIZE - 1, game, Color.WHITE));
        }

        return res;

    }
}
