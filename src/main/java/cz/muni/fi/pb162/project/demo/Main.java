package cz.muni.fi.pb162.project.demo;

import cz.muni.fi.pb162.project.Chess;
import cz.muni.fi.pb162.project.Color;
import cz.muni.fi.pb162.project.Player;

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
    public static void main(String[] args) throws IOException {
        Chess game = new Chess.Builder()
                .addPlayer(new Player("Mat", Color.WHITE))
                .addPlayer(new Player("Pat", Color.BLACK))
                .build();

        game.setInitialSet();

        try {
            game.play();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("RIP");
        }

    }

}
