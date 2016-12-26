package ui.component;

/**
 * Created by Ryan Thomson on 04/11/2016.
 */
public class ITab {

    private final IButton button;
    private final IComponent content;
    private final boolean main; //whether or not this tab should always be at the top

    private boolean displayed = false;

    public ITab(final IButton button, final IComponent content) {
        this(button, content, true);
    }

    public ITab(final IButton button, final IComponent content, final boolean main) {
        assert (button != null);
        this.button = button;
        this.content = content;
        this.main = main;
    }

    public IButton getButton() {
        return this.button;
    }

    public IComponent getContent() {
        return this.content;
    }

    public boolean isMainTab() {
        return this.main;
    }

    // Is the content currently being shown
    public boolean isDisplayed() {
        return this.displayed;
    }

    // Let the tab know that the content is being shown
    void display() {
        this.displayed = true;
    }

    // Let the tab know that it is no longer showing its content
    // If the button is a toggle and is selected, it will automatically deselect it
    void hide() {
        this.displayed = false;
        if(this.button.isToggleButton() && this.button.isSelected()) {
            this.button.click();
        }
    }
}
