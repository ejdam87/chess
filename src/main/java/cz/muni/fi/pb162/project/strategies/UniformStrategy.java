package cz.muni.fi.pb162.project.strategies;

import cz.muni.fi.pb162.project.Board;
import cz.muni.fi.pb162.project.Coordinates;
import cz.muni.fi.pb162.project.Game;
import cz.muni.fi.pb162.project.Piece;
import cz.muni.fi.pb162.project.Player;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;
import java.util.Set;

/**
 * Represents a strategy which selects random piece and move it to random position
 *
 * @author Adam Dzadon
 */
public class UniformStrategy implements MoveStrategy {

    @Override
    public Pair<Coordinates, Coordinates> makeMove(Game game) {

        Player me = game.getCurrentPlayer();
        Board board = game.getBoard();
        Piece[] myPieces = board.getAllByColor(me.color());

        Piece selected;
        Set<Coordinates> possible;

        do {
            int index = new Random().nextInt(myPieces.length);
            selected = myPieces[index];
            possible = game.getMovesByPiece(selected);
        } while (possible.size() == 0);

        Coordinates from = board.findCoordinatesOfPieceById(selected.getId());
        Coordinates to = possible.stream()
                .skip(new Random()
                        .nextInt(possible.size()))
                .findFirst()
                .orElse(null);

        return Pair.of(from, to);
    }
}
