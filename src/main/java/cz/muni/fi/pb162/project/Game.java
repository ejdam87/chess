package cz.muni.fi.pb162.project;

import cz.muni.fi.pb162.project.exceptions.EmptySquareException;
import cz.muni.fi.pb162.project.exceptions.NotAllowedMoveException;
import cz.muni.fi.pb162.project.gui.GameDisplay;
import cz.muni.fi.pb162.project.strategies.ConsolePlayer;
import cz.muni.fi.pb162.project.strategies.MoveStrategy;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Class to represent a board game between two players
 *
 * @author Adam Dzadon
 */
public abstract class Game implements Playable {

    private final Deque<Memento> mementoHistory = new ArrayDeque<>();
    private final Board board;
    private final Player playerOne;
    private final Player playerTwo;
    private MoveStrategy strategyOne = new ConsolePlayer();
    private MoveStrategy strategyTwo = new ConsolePlayer();
    private StateOfGame stateOfGame = StateOfGame.PLAYING;

    // Storing last move to evaluate possibility of "en passant"
    private Pair<Coordinates, Coordinates> lastMove = null;

    protected Game(Player p1,
                   Player p2,
                   Board board,
                   MoveStrategy strategy1,
                   MoveStrategy strategy2) {
        this(p1, p2, board);
        strategyOne = strategy1;
        strategyTwo = strategy2;
    }

    protected Game(Player p1, Player p2, Board board) {
        playerOne = p1;
        playerTwo = p2;
        this.board = board;
    }

    /**
     * Creates new instance of game session
     *
     * @param p1 - instance of Player class ( player n. 1 )
     * @param p2 - instance of Player class ( player n. 2 )
     */
    protected Game(Player p1, Player p2) {
        playerOne = p1;
        playerTwo = p2;
        board = new Board();
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public Board getBoard() {
        return board;
    }

    public StateOfGame getStateOfGame() {
        return stateOfGame;
    }

    public Collection<Memento> getMementoHistory() {
        return Collections.unmodifiableCollection(mementoHistory);
    }

    public void setStateOfGame(StateOfGame stateOfGame) {
        this.stateOfGame = stateOfGame;
    }

    public void setStrategyOne(MoveStrategy strategy) {
        strategyOne = strategy;
    }

    public void setStrategyTwo(MoveStrategy strategy) {
        strategyTwo = strategy;
    }

    /**
     * Returns other player to given player
     *
     * @param player player to get opposite from
     * @return opposite player to given player
     */
    public Player getOtherPlayer(Player player) {
        return player.equals(playerOne) ? playerTwo : playerOne;
    }

    /**
     * Method to get player which is on the move
     *
     * @return player on the move
     */
    public Player getCurrentPlayer() {
        return playerOne.color().ordinal() == board.getRound() % 2 ? playerOne : playerTwo;
    }

    /**
     * Returns the player which is not on the turn
     *
     * @return player not to move
     */
    public Player getOpposingPlayer() {
        return getCurrentPlayer().equals(playerTwo) ? playerOne : playerTwo;
    }

    /**
     * Returns last performed move
     *
     * @return last performed move
     */
    public Pair<Coordinates, Coordinates> getLastMove() {
        return lastMove;
    }

    /**
     * Method to put piece on game board
     *
     * @param letterNumber - row coordinate
     * @param number       - column coordinate
     * @param piece        - piece to be put
     */
    public void putPieceOnBoard(int letterNumber, int number, Piece piece) {
        board.putPieceOnBoard(letterNumber, number, piece);
    }

    /**
     * Returns all coordinates to which can any piece of given player move
     * contains also moves which EXPOSES player's king!!!
     *
     * @param player player to evaluate
     * @return set of coordinates which are possible to move piece to
     */
    public Set<Coordinates> movesByPlayerUnrestricted(Player player) {
        Piece[] pieces = board.getAllByColor(player.color());
        Set<Coordinates> res = new HashSet<>();
        for (Piece piece : pieces) {
            res.addAll(piece.getAllPossibleMoves(this));
        }
        return res;
    }

    /**
     * Returns all possible moves by given player
     *
     * @param player player to evaluate
     * @return all possible moves
     */
    public Set<Coordinates> movesByPlayer(Player player) {
        Piece[] pieces = board.getAllByColor(player.color());
        Set<Coordinates> res = new HashSet<>();
        for (Piece piece : pieces) {
            res.addAll(getMovesByPiece(piece));
        }
        return res;
    }

    /**
     * Returns sum of costs of all pieces of player of given color
     *
     * @param color color of player to evaluate
     * @return sum of costs of all pieces of given color
     */
    public int getTotalValueOf(Color color) {
        Piece[] pieces = board.getAllByColor(color);
        int res = 0;
        for (Piece piece : pieces) {
            res += piece.getPieceType().getValue();
        }

        return res;
    }

    /**
     * Returns all positions to which given piece can move without exposing its king
     *
     * @param piece piece to evaluate
     * @return set of coordinates to which given piece can be moved
     */
    public Set<Coordinates> getMovesByPiece(Piece piece) {

        Coordinates myCoordinates = board.findCoordinatesOfPieceById(piece.getId());
        Set<Coordinates> original = piece.getAllPossibleMoves(this);
        Set<Coordinates> filtered = new HashSet<>();

        for (Coordinates coords : original) {
            Piece previous = board.getPiece(coords);
            move(myCoordinates, coords);
            if (!isCheckOf(getCurrentPlayer())) {
                filtered.add(coords);
            }
            move(coords, myCoordinates);
            putPieceOnBoard(coords.letterNumber(), coords.number(), previous);
        }
        return filtered;
    }

    @Override
    public void move(Coordinates coords1, Coordinates coords2) {

        if (!Board.inRange(coords1) || !Board.inRange(coords2)) {
            return;
        }

        Piece toMove = board.getPiece(coords1);
        if (toMove == null) {
            return;
        }

        putPieceOnBoard(coords1.letterNumber(), coords1.number(), null);
        putPieceOnBoard(coords2.letterNumber(), coords2.number(), toMove);

    }

    /**
     * Method to perform "en passant" move
     *
     * @param from Coordinates of pawn
     * @param to   Coordinates where to move
     */
    public void performEnPassant(Coordinates from, Coordinates to) {
        Coordinates midStop = new Coordinates(from.letterNumber(), to.number());
        move(from, midStop);
        move(midStop, to);
    }

    /**
     * Method to perform "castle" move
     *
     * @param from Coordinates of king
     * @param to   Coordinates where to move king
     */
    public void performCastle(Coordinates from, Coordinates to) {
        move(from, to);

        if (to.number() == 1) {
            move(new Coordinates(from.letterNumber(), 0), new Coordinates(from.letterNumber(), 2));
        } else {
            move(new Coordinates(from.letterNumber(), Board.SIZE - 1), new Coordinates(from.letterNumber(), Board.SIZE - 3));
        }
    }

    /**
     * Checks whether given player's king is endangered
     *
     * @param player player to check
     * @return true if player's king is endangered
     */
    public abstract boolean isCheckOf(Player player);

    /**
     * Method to decide whether there is a winner
     */
    public abstract void updateStatus();

    /**
     * Checks whether the game is still playable
     *
     * @return boolean value representing if the game is still in progress
     */
    public boolean playing() {
        return stateOfGame == StateOfGame.PLAYING || stateOfGame == StateOfGame.CHECK;
    }

    @Override
    public void playRound() throws EmptySquareException, NotAllowedMoveException {
        Player currentPlayer;
        MoveStrategy currentStrategy;
        Map<Player, MoveStrategy> strategies = Map.of(playerOne, strategyOne, playerTwo, strategyTwo);

        currentPlayer = getCurrentPlayer();
        currentStrategy = strategies.get(currentPlayer);

        Pair<Coordinates, Coordinates> coordinates = currentStrategy.makeMove(this);
        Coordinates from = coordinates.getLeft();
        Coordinates to = coordinates.getRight();

        Piece toMove = board.getPiece(from);

        if (!Board.inRange(from)) {
            throw new EmptySquareException("Given coordinates are out of bounds!");
        }
        if (toMove == null) {
            throw new EmptySquareException("Trying to move piece from empty square!");
        }
        if (!getMovesByPiece(toMove).contains(to)) {
            throw new NotAllowedMoveException("Such move is not allowed!");
        }

        // If current wants to pick up piece of opposite player
        if (toMove.getColor() != currentPlayer.color()) {
            return;
        }

        // wants to perform en-passant
        if (toMove.getPieceType() == PieceType.PAWN && to.number() != from.number() && board.isEmpty(to)) {
            performEnPassant(from, to);
            // wants to perform castle
        } else if (toMove.getPieceType() == PieceType.KING && (Math.abs(from.number() - to.number()) > 1)) {
            performCastle(from, to);
            // wants to perform basic move
        } else {
            move(from, to);
        }

        board.setRound(board.getRound() + 1);
        lastMove = Pair.of(from, to);
        updateStatus();
    }

    /**
     * Command-line interface gameplay
     *
     * @throws EmptySquareException    - propagation from inner method
     * @throws NotAllowedMoveException - propagation from inner method
     */
    public void playConsole() throws EmptySquareException, NotAllowedMoveException {
        while (playing()) {
            playRound();
        }
    }

    public void playGUI(GameDisplay display) throws EmptySquareException, NotAllowedMoveException {
        display.showGame();
        while (playing()) {
            try {
                playRound();
            } catch (NotAllowedMoveException | EmptySquareException ex) {
                System.out.println("Not allowed move occurred!");
            }
            display.refresh();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Game game)) {
            return false;
        }
        return Objects.equals(playerOne, game.playerOne) && Objects.equals(playerTwo, game.playerTwo) &&
                stateOfGame == game.stateOfGame && Objects.equals(board, game.board);
    }


    @Override
    public int hashCode() {
        return Objects.hash(playerOne, playerTwo, stateOfGame, board);
    }

    @Override
    public void hitSave() {
        mementoHistory.push(board.save());
    }

    @Override
    public void hitUndo() {
        if (mementoHistory.size() == 0) {
            return;
        }

        board.restore(mementoHistory.pop());
    }

}
