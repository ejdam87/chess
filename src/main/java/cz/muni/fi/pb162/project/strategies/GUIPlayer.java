package cz.muni.fi.pb162.project.strategies;

import cz.muni.fi.pb162.project.Coordinates;
import cz.muni.fi.pb162.project.Game;
import cz.muni.fi.pb162.project.gui.GameDisplay;
import org.apache.commons.lang3.tuple.Pair;

/**
 * GUI human-based move strategy
 *
 * @author Adam Dzadon
 */
public class GUIPlayer implements MoveStrategy {

    private final GameDisplay display;

    public GUIPlayer(GameDisplay display) {
        this.display = display;
    }

    @Override
    public Pair<Coordinates, Coordinates> makeMove(Game game) {
        while (display.getTo() == null || display.getFrom() == null) {
            try{
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }

        Pair<Coordinates, Coordinates> res = Pair.of(display.getFrom(), display.getTo());
        display.eraseSelected();
        return res;
    }
}
