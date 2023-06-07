package cz.muni.fi.pb162.project.strategies;

import cz.muni.fi.pb162.project.Coordinates;
import cz.muni.fi.pb162.project.Game;
import cz.muni.fi.pb162.project.Piece;
import cz.muni.fi.pb162.project.Player;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

/**
 * Represents DFS move strategy, which picks the best move in certain depth with certain cost function
 *
 * @author Adam Dzadon
 */
public class DFStrategy implements MoveStrategy {

    public static final int DEPTH = 2;
    public static final int MIN = 0;
    public static final int MAX = 1;

    private Pair<Pair<Coordinates, Coordinates>, Integer> performDFS(Player me,
                                                                     int state,
                                                                     Game game,
                                                                     Pair<Coordinates, Coordinates> move,
                                                                     int depth) {

        if (depth == 0) {
            int coef = DEPTH % 2 == 0 ? -1 : 1;
            int score = coef * (game.getTotalValueOf(game.getOtherPlayer(me).color()) - game.getTotalValueOf(me.color()));
            return Pair.of(move, score);
        }

        int bestScore = state == MAX ? -10000000 : 100000000;
        Pair<Coordinates, Coordinates> bestMove = null;

        Piece[] myPieces = game.getBoard().getAllByColor(me.color());
        for (Piece piece : myPieces) {
            Coordinates pieceCoordinates = game.getBoard().findCoordinatesOfPieceById(piece.getId());
            Set<Coordinates> to = game.getMovesByPiece(piece);
            for (Coordinates coordinates : to) {
                Piece previous = game.getBoard().getPiece(coordinates);
                game.move(pieceCoordinates, coordinates);

                Pair<Coordinates, Coordinates> moveDone = Pair.of(pieceCoordinates, coordinates);
                int newState = state == MAX ? MIN : MAX;
                Pair<Pair<Coordinates, Coordinates>, Integer> res = performDFS(game.getOtherPlayer(me), newState, game, moveDone, depth - 1);

                if ((state == MAX && res.getRight() > bestScore) || (state == MIN && res.getRight() < bestScore)) {
                    bestScore = res.getRight();
                    bestMove = moveDone;
                }

                game.putPieceOnBoard(pieceCoordinates.letterNumber(), pieceCoordinates.number(), piece);
                game.putPieceOnBoard(coordinates.letterNumber(), coordinates.number(), previous);
            }

        }
        return Pair.of(bestMove, bestScore);
    }

    private Pair<Coordinates, Coordinates> getBestInDepth(Game game) {
        Pair<Pair<Coordinates, Coordinates>, Integer> res = performDFS(game.getCurrentPlayer(), MAX, game, null, DEPTH);
        return res.getLeft();
    }

    @Override
    public Pair<Coordinates, Coordinates> makeMove(Game game) {
        return getBestInDepth(game);
    }

}
