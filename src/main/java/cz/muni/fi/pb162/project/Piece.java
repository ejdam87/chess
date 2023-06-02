package cz.muni.fi.pb162.project;

import cz.muni.fi.pb162.project.moves.Diagonal;
import cz.muni.fi.pb162.project.moves.Jump;
import cz.muni.fi.pb162.project.moves.Knight;
import cz.muni.fi.pb162.project.moves.Move;
import cz.muni.fi.pb162.project.moves.Pawn;
import cz.muni.fi.pb162.project.moves.Straight;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Class representing a single piece of the board game.
 *
 * @author Adam Dzadon
 */
public class Piece implements Prototype<Piece> {
    private final long id;
    private final List<Move> moves;
    private final Color color;
    private final PieceType pieceType;
    private static final AtomicLong ID_GETTER = new AtomicLong();

    /**
     * Creates a new piece
     */
    public Piece(Color color, PieceType pieceType) {
        this.color = color;
        this.pieceType = pieceType;
        this.id = ID_GETTER.incrementAndGet();
        this.moves = getMovesByType(pieceType);
    }

    /**
     * Creates a new piece as a clone of other
     *
     * @param other - other piece instance
     */
    public Piece(Piece other) {
        this.color = other.color;
        this.pieceType = other.pieceType;
        this.id = other.id;
        this.moves = other.moves;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public long getId() {
        return this.id;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Method to get all possible moves for this piece in given game
     *
     * @param game instance of current game
     * @return set of coordinates of all possible moves
     */
    public Set<Coordinates> getAllPossibleMoves(Game game) {
        Coordinates myCoordinates = game.getBoard().findCoordinatesOfPieceById(id);
        Stream<Move> strategies = getMoves().stream();

        return strategies.collect(
                HashSet::new,
                (res, strategy) -> res.addAll(strategy.getAllowedMoves(game, myCoordinates)),
                Set::addAll
        );

    }

    @Override
    public Piece makeClone() {
        return new Piece(color, pieceType);
    }

    @Override
    public String toString() {
        return pieceType.getSymbol(color);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Piece other)) {
            return false;
        }
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Private method to get a list of moves based on given pieceType
     *
     * @param pieceType type of given piece
     * @return list of moves for given type
     */
    private List<Move> getMovesByType(PieceType pieceType) {
        List<Move> moves;
        switch (pieceType) {
            case KING -> moves = List.of(new Straight(1), new Diagonal(1));
            case QUEEN -> moves = List.of(new Straight(), new Diagonal());
            case BISHOP -> moves = List.of(new Diagonal());
            case ROOK -> moves = List.of(new Straight());
            case KNIGHT -> moves = List.of(new Knight());
            case PAWN -> moves = List.of(new Pawn());
            case DRAUGHTS_KING -> moves = List.of(new Diagonal(1), new Jump());
            case DRAUGHTS_MAN -> moves = List.of(new Diagonal(1, true), new Jump(true));
            default -> throw new IllegalArgumentException("Unknown type in chess.");
        }

        return moves;
    }
}
