package cz.muni.fi.pb162.project;

/**
 * Record to store the state of the board game
 *
 * @param round - round to be stored
 * @param board - 2D array representing a board to be saved
 * @author Adam Dzadon
 */
public record Memento(int round, Piece[][] board) {
}
