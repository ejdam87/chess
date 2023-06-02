package cz.muni.fi.pb162.project;

/**
 * Class representing player of the board game.
 *
 * @param name  - name of the player
 * @param color - color of the player
 * @author Adam Dzadon
 */
public record Player(String name, Color color) {

    @Override
    public String toString() {
        return this.name() + "-" + this.color();
    }
}
