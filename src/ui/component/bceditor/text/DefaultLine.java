package ui.component.bceditor.text;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class DefaultLine extends Line {

    public DefaultLine(final String string) {
        this(string, 0);
    }

    public DefaultLine(final String string, final int indent) {
        super(string, indent);
    }

    @Override
    public void stylize() {
        Line.stylize(super.getString(), super.attributes);
    }
}