package cz.muni.fi.pb162.project.strategies;

import cz.muni.fi.pb162.project.Coordinates;
import cz.muni.fi.pb162.project.Game;
import cz.muni.fi.pb162.project.exceptions.InvalidFormatOfInputException;
import cz.muni.fi.pb162.project.utils.BoardNotation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Scanner;

/**
 * @author Adam Dzadon
 */
public class ConsolePlayer implements MoveStrategy {

    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Method to load command-line input from player
     *
     * @return Coordinates of given input
     */
    private Coordinates getInputFromPlayer() {

        var position = SCANNER.next().trim();
        var letterNumber = position.charAt(0);

        if (!Character.isAlphabetic(letterNumber)) {
            throw new InvalidFormatOfInputException("Invalid letter number provided!");
        }

        if (!Character.isDigit(position.charAt(1))) {
            throw new InvalidFormatOfInputException("Invalid number provided");
        }

        var number = Integer.parseInt(String.valueOf(position.charAt(1)));

        return BoardNotation.getCoordinatesOfNotation(letterNumber, number);
    }

    @Override
    public Pair<Coordinates, Coordinates> makeMove(Game game) {
        return Pair.of(getInputFromPlayer(), getInputFromPlayer());
    }

}
