package ui.component;

import project.filetype.ClassType;

import java.awt.*;

/**
 * Created by Ryan Thomson on 27/12/2016.
 */
public class IBCEditor extends IEditor {

    private final ClassType classType;

    public IBCEditor(final ClassType classType){
        this.classType = classType;

        super.setBackground(Color.ORANGE);
        super.add(new IButton(classType.getFullPath()));
    }
}
