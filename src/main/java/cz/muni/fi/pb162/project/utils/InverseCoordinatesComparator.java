package cz.muni.fi.pb162.project.utils;

import cz.muni.fi.pb162.project.Coordinates;

import java.util.Comparator;

/**
 * Implementation of comparator for inverse ordering on Coordinates
 *
 * @author Adam Dzadon
 */
public class InverseCoordinatesComparator implements Comparator<Coordinates> {
    @Override
    public int compare(Coordinates o1, Coordinates o2) {
        return -o1.compareTo(o2);
    }
}
