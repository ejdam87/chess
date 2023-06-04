package cz.muni.fi.pb162.project.gui;

import cz.muni.fi.pb162.project.Board;
import cz.muni.fi.pb162.project.Coordinates;
import cz.muni.fi.pb162.project.Game;
import cz.muni.fi.pb162.project.Piece;

import javax.swing.*;
import java.awt.*;

/**
 * Class representing GUI display of chess game
 *
 * @author Adam Dzadon
 */
public class GameDisplay extends JFrame {

    Coordinates fromSelected;
    Coordinates toSelected;

    Game game;
    JButton[][] boardShow = new JButton[Board.SIZE][Board.SIZE];
    GridLayout layout;
    JPanel panel;

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

    private void initBoardGrid() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Piece actual = game.getBoard().getPiece(i, j);
                String toShow = "";
                if (actual != null) {
                    toShow = actual.toString();
                }
                boardShow[i][j] = new JButton(toShow);

                int row = i;
                int col = j;
                boardShow[i][j].addActionListener(e -> buttonClicked(row, col));
            }
        }
    }

    private void addCells() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                panel.add(boardShow[i][j]);
            }
        }
    }

    public GameDisplay(Game game) {
        this.game = game;
        initBoardGrid();
        layout = new GridLayout(Board.SIZE, Board.SIZE);
        panel = new JPanel();
        panel.setLayout(layout);
        addCells();
    }

    public void buttonClicked(int i, int j) {

        System.out.println(toSelected);

        if (fromSelected == null) {
            fromSelected = new Coordinates(i, j);
        } else if (toSelected == null) {
            toSelected = new Coordinates(i, j);
        }
    }

    public void refresh() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                String toShow = "";
                Piece actual = game.getBoard().getPiece(i, j);
                if (actual != null) {
                    toShow = actual.toString();
                }

                boardShow[i][j].setText(toShow);
            }
        }
    }

    public void showGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        add(panel);
        setVisible(true);
    }

}
