package cz.muni.fi.pb162.project.moves;

import cz.muni.fi.pb162.project.Board;
import cz.muni.fi.pb162.project.Color;
import cz.muni.fi.pb162.project.Coordinates;
import cz.muni.fi.pb162.project.Game;
import cz.muni.fi.pb162.project.Piece;
import cz.muni.fi.pb162.project.PieceType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Movement of a chess pawn.
 *
 * @author Alzbeta Strompova
 */
public class Pawn implements Move {

    /**
     * Decides whether the last move allows us to perform en passant move
     *
     * @param from coordinates from which pawn moved
     * @param to   coordinates to which pawn moved
     * @param me   my coordinates
     * @return true if we are able to do en passant
     */
    private boolean isEnPassant(Coordinates from, Coordinates to, Coordinates me) {
        return Math.abs(from.letterNumber() - to.letterNumber()) > 1
                && me.letterNumber() == to.letterNumber()
                && Math.abs(me.number() - to.number()) == 1;
    }

    @Override
    public Set<Coordinates> getAllowedMoves(Game game, Coordinates position) {
        var board = game.getBoard();
        var result = new HashSet<Coordinates>();
        var piece = board.getPiece(position);
        if (piece != null && piece.getColor().equals(Color.WHITE)) {
            movesByWhite(board, position, result);
        }
        if (piece != null && piece.getColor().equals(Color.BLACK)) {
            movesByBlack(board, position, result);
        }

        Pair<Coordinates, Coordinates> lastMove = game.getLastMove();

        if (lastMove == null) {
            return result;
        }

        Coordinates from = lastMove.getLeft();
        Coordinates to = lastMove.getRight();
        Piece possiblePawn = game.getBoard().getPiece(to);

        if (possiblePawn == null) {
            return result;
        }

        if (possiblePawn.getPieceType() == PieceType.PAWN && isEnPassant(from, to, position)) {
            int letterNumber = (from.letterNumber() + to.letterNumber()) / 2;
            int number = from.number();
            result.add(new Coordinates(letterNumber, number));
        }

        return result;
    }

    private void movesByWhite(Board board, Coordinates position, HashSet<Coordinates> result) {
        if (board.isEmpty(position.letterNumber() - 1, position.number())) {
            result.add(new Coordinates(position.letterNumber() - 1, position.number()));
        }
        if (position.letterNumber() == Board.SIZE - 2 && board.isEmpty(position.letterNumber() - 2, position.number())) {
            result.add(new Coordinates(position.letterNumber() - 2, position.number()));
        }
        if (!board.isEmpty(position.letterNumber() - 1, position.number() + 1)
                && board.getPiece(position.letterNumber() - 1, position.number() + 1).getColor().equals(Color.BLACK)) {
            result.add(new Coordinates(position.letterNumber() - 1, position.number() + 1));
        }
        if (!board.isEmpty(position.letterNumber() - 1, position.number() - 1)
                && board.getPiece(position.letterNumber() - 1, position.number() - 1).getColor().equals(Color.BLACK)) {
            result.add(new Coordinates(position.letterNumber() - 1, position.number() - 1));
        }
    }

    private void movesByBlack(Board board, Coordinates position, HashSet<Coordinates> result) {
        if (board.isEmpty(position.letterNumber() + 1, position.number())) {
            result.add(new Coordinates(position.letterNumber() + 1, position.number()));
        }

        if (position.letterNumber() == 1 && board.isEmpty(position.letterNumber() + 2, position.number())) {
            result.add(new Coordinates(position.letterNumber() + 2, position.number()));
        }
        if (!board.isEmpty(position.letterNumber() + 1, position.number() + 1)
                && board.getPiece(position.letterNumber() + 1, position.number() + 1).getColor().equals(Color.WHITE)) {
            result.add(new Coordinates(position.letterNumber() + 1, position.number() + 1));
        }
        if (!board.isEmpty(position.letterNumber() + 1, position.number() - 1)
                && board.getPiece(position.letterNumber() + 1, position.number() - 1).getColor().equals(Color.WHITE)) {
            result.add(new Coordinates(position.letterNumber() + 1, position.number() - 1));
        }
    }
}

