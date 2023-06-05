package cz.muni.fi.pb162.project;

import cz.muni.fi.pb162.project.utils.IconLoader;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing type of particular pieces
 *
 * @author Adam Dzadon
 */
public enum PieceType {

    // Chess
    KING,
    QUEEN,
    ROOK,
    BISHOP,
    KNIGHT,
    PAWN,

    // Draughts
    DRAUGHTS_MAN,
    DRAUGHTS_KING;

    private static final Map<Pair<PieceType, Color>, ImageIcon> ICONS = new HashMap<>() {{
        put(Pair.of(PieceType.KING, Color.WHITE), IconLoader.loadImage("textures/king_white.png"));
        put(Pair.of(PieceType.QUEEN, Color.WHITE), IconLoader.loadImage("textures/queen_white.png"));
        put(Pair.of(PieceType.BISHOP, Color.WHITE), IconLoader.loadImage("textures/bishop_white.png"));
        put(Pair.of(PieceType.ROOK, Color.WHITE), IconLoader.loadImage("textures/rook_white.png"));
        put(Pair.of(PieceType.KNIGHT, Color.WHITE), IconLoader.loadImage("textures/knight_white.png"));
        put(Pair.of(PieceType.PAWN, Color.WHITE), IconLoader.loadImage("textures/pawn_white.png"));
        put(Pair.of(PieceType.KING, Color.BLACK), IconLoader.loadImage("textures/king_black.png"));
        put(Pair.of(PieceType.QUEEN, Color.BLACK), IconLoader.loadImage("textures/queen_black.png"));
        put(Pair.of(PieceType.BISHOP, Color.BLACK), IconLoader.loadImage("textures/bishop_black.png"));
        put(Pair.of(PieceType.ROOK, Color.BLACK), IconLoader.loadImage("textures/rook_black.png"));
        put(Pair.of(PieceType.KNIGHT, Color.BLACK), IconLoader.loadImage("textures/knight_black.png"));
        put(Pair.of(PieceType.PAWN, Color.BLACK), IconLoader.loadImage("textures/pawn_black.png"));
    }};

    private static final Map<Pair<PieceType, Color>, String> SYMBOLS = new HashMap<>() {{
        put(Pair.of(PieceType.KING, Color.WHITE), "♔");
        put(Pair.of(PieceType.QUEEN, Color.WHITE), "♕");
        put(Pair.of(PieceType.BISHOP, Color.WHITE), "♗");
        put(Pair.of(PieceType.ROOK, Color.WHITE), "♖");
        put(Pair.of(PieceType.KNIGHT, Color.WHITE), "♘");
        put(Pair.of(PieceType.PAWN, Color.WHITE), "♙");
        put(Pair.of(PieceType.KING, Color.BLACK), "♚");
        put(Pair.of(PieceType.QUEEN, Color.BLACK), "♛");
        put(Pair.of(PieceType.BISHOP, Color.BLACK), "♝");
        put(Pair.of(PieceType.ROOK, Color.BLACK), "♜");
        put(Pair.of(PieceType.KNIGHT, Color.BLACK), "♞");
        put(Pair.of(PieceType.PAWN, Color.BLACK), "♟");
        put(Pair.of(PieceType.DRAUGHTS_MAN, Color.WHITE), "⛀");
        put(Pair.of(PieceType.DRAUGHTS_KING, Color.WHITE), "⛁");
        put(Pair.of(PieceType.DRAUGHTS_MAN, Color.BLACK), "⛂");
        put(Pair.of(PieceType.DRAUGHTS_KING, Color.BLACK), "⛃");
    }};

    /**
     * Method to return unicode representation of given piece type with given color
     *
     * @param color color of piece type
     * @return unicode representation of piece type with color
     */
    public String getSymbol(Color color) {
        return SYMBOLS.getOrDefault(Pair.of(this, color), "");
    }

    public ImageIcon getIcon(Color color) {
        return ICONS.getOrDefault(Pair.of(this, color), null);
    }
}
