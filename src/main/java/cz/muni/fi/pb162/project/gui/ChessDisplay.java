package cz.muni.fi.pb162.project.gui;

import cz.muni.fi.pb162.project.Board;
import cz.muni.fi.pb162.project.Chess;
import cz.muni.fi.pb162.project.Piece;

import javax.swing.*;
import java.awt.*;

/**
 * Class representing GUI display of chess game
 *
 * @author Adam Dzadon
 */
public class ChessDisplay extends JFrame {
    Chess game;
    JButton[][] boardShow = new JButton[Board.SIZE][Board.SIZE];
    GridLayout layout;
    JPanel panel;

    private void initBoardGrid() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Piece actual = game.getBoard().getPiece(i, j);
                String toShow = "";
                if (actual != null) {
                    toShow = actual.toString();
                }
                boardShow[i][j] = new JButton(toShow);
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

    public ChessDisplay(Chess game) {
        this.game = game;
        initBoardGrid();
        layout = new GridLayout(Board.SIZE, Board.SIZE);
        panel = new JPanel();
        panel.setLayout(layout);
        addCells();
    }

    public void showGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        add(panel);
        setVisible(true);
    }

}
