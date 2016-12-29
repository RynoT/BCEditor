package ui.component.bceditor;

import ui.component.IBCEditor;
import ui.component.IComponent;
import ui.component.IScrollPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan Thomson on 29/12/2016.
 */
public class IBCTextEditor extends IComponent {

    public static final int LINE_HEIGHT = 16;

    private final TextPanel textPanel;

    public IBCTextEditor(){
        super.setOpaque(false);
        super.setLayout(new BorderLayout(0, 0));

        super.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        super.setPreferredSize(new Dimension(Integer.MAX_VALUE, 0));

        this.textPanel = new TextPanel();
        final IScrollPanel scrollPanel = new IScrollPanel(this.textPanel, false, true);
        {
            scrollPanel.setOpaque(false);
        }
        super.add(scrollPanel, BorderLayout.CENTER);
    }

    public void setText(final String text){
        this.textPanel.text = text;

        this.textPanel.lineCount = text.split("\n").length;
        this.textPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, this.textPanel.lineCount * IBCTextEditor.LINE_HEIGHT));

        super.revalidate();
        super.repaint();
    }

    private class TextPanel extends JPanel {

        private String text = "";
        private int lineCount = 0;

        private TextPanel(){
            super.setOpaque(false);
            super.setFont(IBCEditor.EDITOR_FONT);
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            final Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setFont(super.getFont());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final int textHeight = g.getFontMetrics().getHeight() / 2;
            final String[] lines = this.text.split("\n");
            for(int i = 0; i < lines.length; i++){
                g2d.drawString(lines[i], 0, IBCTextEditor.LINE_HEIGHT * (i + 1) - (IBCTextEditor.LINE_HEIGHT - textHeight) / 2);
            }
        }
    }
}
