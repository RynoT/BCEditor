package ui.component;

import java.awt.*;

/**
 * Created by Ryan Thomson on 04/11/2016.
 */
public enum IOrientation {

    NORTH(BorderLayout.NORTH, 0),
    EAST(BorderLayout.EAST, 270),
    SOUTH(BorderLayout.SOUTH, 180),
    WEST(BorderLayout.WEST, 90);

    private final String border;
    private final int rotation; //degrees

    IOrientation(final String border, final int rotation){
        this.border = border;
        this.rotation = rotation;
    }

    public String getBorder() {
        return this.border;
    }

    public int getDegrees(){
        return this.rotation;
    }

    public float getRadians(){
        return (float)Math.toRadians(this.rotation);
    }

    public boolean isVertical(){
        return this.rotation == 0 || this.rotation == 180;
    }

    public boolean isHorizontal(){
        return this.rotation == 90 || this.rotation == 270;
    }

    public static IOrientation getNext(final IOrientation current){
        switch(current){
            case NORTH:
                return IOrientation.EAST;
            case EAST:
                return IOrientation.SOUTH;
            case SOUTH:
                return IOrientation.WEST;
            case WEST:
                return IOrientation.NORTH;
        }
        assert(false);
        return current;
    }

    public static IOrientation getOpposite(final IOrientation current){
        switch(current){
            case NORTH:
                return IOrientation.SOUTH;
            case EAST:
                return IOrientation.WEST;
            case SOUTH:
                return IOrientation.NORTH;
            case WEST:
                return IOrientation.EAST;
        }
        assert(false);
        return current;
    }
}
