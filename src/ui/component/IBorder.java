package ui.component;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * Created by Ryan Thomson on 15/12/2016.
 */
public class IBorder extends AbstractBorder {

    private final int north, east, south, west;

    private boolean enableInset = true;
    private Color primary = IComponent.DEFAULT_HIGHLIGHT_DARK, secondary = IComponent.DEFAULT_HIGHLIGHT_LIGHT;

    public IBorder(final int north, final int east, final int south, final int west) {
        assert(north >= 0 && north <= 2 && east >= 0 && east <= 2
                && south >= 0 && south <= 2 && west >= 0 && west <= 2);
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    public void setEnableInset(final boolean enable){
        this.enableInset = enable;
    }

    public void setPrimaryHighlight(final Color color){
        assert(color != null);
        this.primary = color;
    }

    public void setSecondaryHighlight(final Color color){
        assert(color != null);
        this.secondary = color;
    }

    @Override
    public void paintBorder(final Component component, final Graphics g, final int x, final int y, final int width, final int height) {
        if(this.north > 0) { //top left to top right
            g.setColor(this.north == 1 ? this.primary : this.secondary);
            g.drawLine(x + this.west - 1, y, x + width - 1 - this.east + 1, y);
            if(this.north != 1){
                g.setColor(this.primary);
                g.drawLine(x + this.west - 1, y + 1, x + width - 1 - this.east + 1, y + 1);
            }
        }
        if(this.east > 0){ //top right to bottom right
            g.setColor(this.east == 1 ? this.primary : this.secondary);
            g.drawLine(x + width - 1, y + this.north - 1, x + width - 1, y + height - 1 - this.south + 1);
            if(this.east != 1){
                g.setColor(this.primary);
                g.drawLine(x + width - 2, y + this.north - 1, x + width - 2, y + height - 1 - this.south + 1);
            }
        }
        if(this.south > 0){ //bottom right to bottom left
            g.setColor(this.south == 1 ? this.primary : this.secondary);
            g.drawLine(x + width - 1 - this.east + 1, y + height - 1, x + this.west - 1, y + height - 1);
            if(this.south != 1){
                g.setColor(this.primary);
                g.drawLine(x + width - 1 - this.east + 1, y + height - 2, x + this.west - 1, y + height - 2);
            }
        }
        if(this.west > 0){ //bottom left to top left
            g.setColor(this.west == 1 ? this.primary : this.secondary);
            g.drawLine(x, y + height - 1 - this.south + 1, x, y + this.north - 1);
            if(this.west != 1){
                g.setColor(this.primary);
                g.drawLine(x + 1, y + height - 1 - this.south + 1, x + 1, y + this.north - 1);
            }
        }
    }

    @Override
    public Insets getBorderInsets(final Component component, final Insets insets) {
        if(this.enableInset) {
            insets.set(this.north, this.west, this.south, this.east);
        } else {
            insets.set(0, 0, 0, 0);
        }
        return insets;
    }
}