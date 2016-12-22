package ui.component.explorer;

import ui.component.IComponent;
import ui.component.ILabel;
import ui.component.ITextAlign;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan Thomson on 21/12/2016.
 */
public abstract class ITileNode extends IComponent {

    public static final int TILE_SIZE = 20; //height
    public static final int TILE_DEFAULT_INSET = 5;
    public static final int TILE_DEFAULT_TEXT_PADDING = 2;
    public static final int TILE_FILE_INSET = 31 - ITileNode.TILE_DEFAULT_TEXT_PADDING;
    public static final int TILE_FOLDER_INSET = 10;

    protected final String name;
    protected final ILabel label;
    protected final IFolderNode parent;

    protected final Component inset;

    ITileNode(final String name, final IFolderNode parent, int inset) {
        assert (name != null);
        this.name = name;
        this.parent = parent;
        this.label = new ILabel(name);
        this.label.setAlignment(ITextAlign.LEFT);
        this.label.setPadding(0, 0, 0, ITileNode.TILE_DEFAULT_TEXT_PADDING);

        super.setOpaque(false);
        super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        super.setPreferredSize(new Dimension(0, ITileNode.TILE_SIZE));
        super.setMinimumSize(super.getPreferredSize());
        super.setMaximumSize(new Dimension(Integer.MAX_VALUE, ITileNode.TILE_SIZE));

        if(parent != null){
            if(parent.isRootNode()){
                inset = 0;
            } else {
                inset += parent.inset.getPreferredSize().width;
            }
        }
        this.inset = Box.createHorizontalStrut(inset + ITileNode.TILE_DEFAULT_INSET);
        super.add(this.inset);
        this.init();
        super.add(this.label);
    }

    public abstract void init();

    public boolean isRootNode() {
        return this.parent == null;
    }

    public IFolderNode getParentNode() {
        return this.parent;
    }
}
