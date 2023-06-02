package cz.muni.fi.pb162.project.strategies;

import cz.muni.fi.pb162.project.Coordinates;
import cz.muni.fi.pb162.project.Game;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Represents moving strategy
 *
 * @author Adam Dzadon
 */
public interface MoveStrategy {
    Pair<Coordinates, Coordinates> makeMove(Game game);
}
