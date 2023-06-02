package cz.muni.fi.pb162.project;

/**
 * @author Adam Dzadon
 */
public enum StateOfGame {
    WHITE_PLAYER_WIN,
    BLACK_PLAYER_WIN,
    PAT,
    PLAYING;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
