package cz.muni.fi.pb162.project.demo;

import cz.muni.fi.pb162.project.Chess;
import cz.muni.fi.pb162.project.Color;
import cz.muni.fi.pb162.project.Player;
import cz.muni.fi.pb162.project.exceptions.EmptySquareException;
import cz.muni.fi.pb162.project.exceptions.NotAllowedMoveException;
import cz.muni.fi.pb162.project.gui.GameDisplay;
import cz.muni.fi.pb162.project.strategies.GUIPlayer;
import cz.muni.fi.pb162.project.strategies.UniformStrategy;

import java.io.IOException;

/**
 * Class for running main method.
 *
 * @author Alzbeta Strompova
 */
public class Main {


    /**
     * Runs the code.
     *
     * @param args command line arguments, will be ignored.
     */
    public static void main(String[] args) throws IOException, EmptySquareException, NotAllowedMoveException {
        Chess game = new Chess.Builder()
                .addPlayer(new Player("Mat", Color.WHITE))
                .addPlayer(new Player("Pat", Color.BLACK))
                .addStrategy(new UniformStrategy())
                .addStrategy(new UniformStrategy())
                .build();

        game.setInitialSet();
        GameDisplay disp = new GameDisplay(game);
        game.setStrategyTwo(new GUIPlayer(disp));
        game.playGUI(disp);

    }

}
