package cz.muni.fi.pb162.project;

/**
 * @author Adam Dzadon
 */
public enum StateOfGame {
    MATE,
    PAT,
    CHECK,
    PLAYING;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
