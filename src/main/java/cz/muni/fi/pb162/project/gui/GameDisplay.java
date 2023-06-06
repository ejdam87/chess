package cz.muni.fi.pb162.project.gui;

import cz.muni.fi.pb162.project.Board;
import cz.muni.fi.pb162.project.Coordinates;
import cz.muni.fi.pb162.project.Game;
import cz.muni.fi.pb162.project.Piece;

import javax.swing.*;
import java.awt.*;

/**
 * Class representing GUI display of a board game
 *
 * @author Adam Dzadon
 */
public class GameDisplay extends JFrame {

    private Coordinates fromSelected;
    private Coordinates toSelected;

    private final static Color DARK = new Color(61, 115, 66);
    private final static Color LIGHT = new Color(240, 243, 150);


    private final Game game;
    private final JButton[][] boardShow = new JButton[Board.SIZE][Board.SIZE];
    private final JPanel panel;
    private final JLabel status;

    public GameDisplay(Game game) {
        this.game = game;
        initBoardGrid();
        GridLayout layout = new GridLayout(Board.SIZE, Board.SIZE);
        panel = new JPanel();
        panel.setLayout(layout);
        status = new JLabel("Ola", SwingConstants.CENTER);
        addCells();
        refresh();
    }

    /**
     * deletes selection of cells on board
     */
    public void eraseSelected() {
        fromSelected = null;
        toSelected = null;
    }

    public Coordinates getFrom() {
        return fromSelected;
    }

    public Coordinates getTo() {
        return toSelected;
    }

    /**
     * Initializes the grid of buttons representing board cells
     */
    private void initBoardGrid() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                boardShow[i][j] = new JButton();

                if ((i + j) % 2 == 0) {
                    boardShow[i][j].setBackground(GameDisplay.LIGHT);
                } else {
                    boardShow[i][j].setBackground(GameDisplay.DARK);
                }

                boardShow[i][j].setFont(new Font("Sans-Serif", Font.PLAIN, 60));

                int row = i;
                int col = j;
                boardShow[i][j].addActionListener(e -> buttonClicked(row, col));
            }
        }
    }

    /**
     * Fills the panel to show the grid
     */
    private void addCells() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                panel.add(boardShow[i][j]);
            }
        }
    }

    /**
     * Action handler of button click
     *
     * @param i row of button
     * @param j col of button
     */
    public void buttonClicked(int i, int j) {
        if (fromSelected == null) {
            fromSelected = new Coordinates(i, j);
        } else if (toSelected == null) {
            toSelected = new Coordinates(i, j);
        }

        refreshStatus();

    }

    private void refreshStatus() {
        String space = "   ";
        status.setText(
                "From:"
                        + fromSelected
                        + space
                        + "To:"
                        + toSelected
                        + space
                        + "State:"
                        + game.getStateOfGame()
                        + space
                        + "On move:"
                        + game.getCurrentPlayer().color());
    }

    /**
     * Changes labels of buttons according to state of game board
     */
    public void refresh() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                ImageIcon toShow = null;
                Piece actual = game.getBoard().getPiece(i, j);
                if (actual != null) {
                    toShow = actual.toIcon();
                }
                boardShow[i][j].setIcon(toShow);
            }
        }

        refreshStatus();
    }

    /**
     * Sets up the display
     */
    public void showGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 800);
        add(panel, BorderLayout.NORTH);
        add(status, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

}
