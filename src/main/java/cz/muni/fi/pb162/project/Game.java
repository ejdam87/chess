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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

    protected Game(Player p1, Player p2, Board board, MoveStrategy move1, MoveStrategy move2) {
        this(p1, p2, board);
        strategyOne = move1;
        strategyTwo = move2;
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
     * Method to get player which is on the move
     *
     * @return player on the move
     */
    public Player getCurrentPlayer() {
        return playerOne.color().ordinal() == board.getRound() % 2 ? playerOne : playerTwo;
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
     * A method to get all possible moves by current player sorted in specific way
     *
     * @return a set of all possible move by current player sorted in inverse ordering on pieces
     */
    public Set<Coordinates> allPossibleMovesByCurrentPlayer() {
        SortedSet<Coordinates> res = new TreeSet<>((o1, o2) -> -o1.compareTo(o2));

        for (Piece piece : board.getAllPiecesFromBoard()) {
            if (piece.getColor() == getCurrentPlayer().color()) {
                res.addAll(piece.getAllPossibleMoves(this));
            }
        }
        return res;
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
     * Method to decide whether there is a winner
     */
    public abstract void updateStatus();

    /**
     * A method to check whether desired move is possible
     *
     * @param piece piece to make a move with
     * @param to    coordinates to move to
     * @return true if possible false otherwise
     */
    private boolean checkMove(Piece piece, Coordinates to) {
        return Board.inRange(to) && piece.getAllPossibleMoves(this).contains(to);
    }

    /**
     * Checks whether the game is still playable
     *
     * @return boolean value representing if the game is still in progress
     */
    public boolean playing() {
        return stateOfGame == StateOfGame.PLAYING;
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

        if (!Board.inRange(from)) {
            throw new EmptySquareException("Given coordinates are out of bounds!");
        }
        if (board.getPiece(from) == null) {
            throw new EmptySquareException("Trying to move piece from empty square!");
        }
        if (!checkMove(board.getPiece(from), to)) {
            throw new NotAllowedMoveException("Such move is not allowed!");
        }

        // If current wants to pick up piece of opposite player
        if (board.getPiece(from).getColor() != currentPlayer.color()) {
            return;
        }

        move(from, to);
        board.setRound(board.getRound() + 1);
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
            playRound();
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
