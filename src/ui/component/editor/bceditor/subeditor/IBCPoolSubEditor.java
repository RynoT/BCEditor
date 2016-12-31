package ui.component.editor.bceditor.subeditor;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;
import ui.component.IBorder;
import ui.component.IComponent;
import ui.component.ILabel;
import ui.component.IScrollPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class IBCPoolSubEditor extends IBCSubEditor {

    public static final int ROW_HEIGHT = 20;

    public static final int PADDING = 6;
    public static final int NAME_WIDTH = 100;

    public static final Color LINE_COLOR = new Color(63, 64, 65);
    public static final Color BACKGROUND_COLOR = new Color(53, 54, 55);

    private final JPanel titlePanel;
    private final JPanel tablePanel;

    private int indexWidth = 0, bitWidth = 0;

    public IBCPoolSubEditor(){
        super.setFont(IComponent.DEFAULT_FONT);
        super.setLayout(new BorderLayout(0, 0));
        super.setBackground(IBCPoolSubEditor.BACKGROUND_COLOR);

        final JPanel titlePanel = new JPanel();
        {
            titlePanel.setLayout(new BorderLayout(0, 0));
            titlePanel.setBackground(IComponent.DEFAULT_BACKGROUND_HIGHLIGHT);
            titlePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, IBCSubEditor.TITLE_HEIGHT));
            titlePanel.setBorder(new IBorder(0, 1, 1, 0));

            final ILabel label = new ILabel("Constant Pool");
            label.setFont(IBCSubEditor.TITLE_FONT);
            label.setColor(IBCSubEditor.TITLE_TEXT_COLOR);
            titlePanel.add(label, BorderLayout.CENTER);
        }
        this.titlePanel = titlePanel;
        super.add(titlePanel, BorderLayout.NORTH);

        final JPanel tablePanel = new JPanel();
        {
            tablePanel.setOpaque(false);
            tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        }
        this.tablePanel = tablePanel;
        final IScrollPanel scrollPanel = new IScrollPanel(this.tablePanel, true, true);
        {
            scrollPanel.setOpaque(false);
        }
        super.add(scrollPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getTitlePanel(){
        return this.titlePanel;
    }

    public void populate(final ConstantPool pool){
        assert (pool != null);

        final FontMetrics metrics = super.getFontMetrics(super.getFont());
        this.indexWidth = metrics.stringWidth(String.valueOf(pool
                .getEntryCount())) + IBCPoolSubEditor.PADDING * 2;

        this.tablePanel.removeAll();
        for(int i = 0; i < pool.getEntryCount(); i++){
            final PoolTag tag = pool.getEntry(i);
            this.tablePanel.add(new PoolRow(i, tag.getPoolTagName(), tag.getPoolTagBitCount(), tag.getContentString(pool)));

            this.bitWidth = Math.max(this.bitWidth, metrics.stringWidth(String
                    .valueOf(tag.getPoolTagBitCount())) + IBCPoolSubEditor.PADDING * 2);
        }
        this.tablePanel.setPreferredSize(new Dimension(100, this.tablePanel.getComponentCount() * IBCPoolSubEditor.ROW_HEIGHT));

        this.tablePanel.revalidate();
        this.tablePanel.repaint();
    }

    private class PoolRow extends JPanel {

        private int index, bitCount;
        private final String name;
        private String value;

        private boolean pressed = false, hovered = false;

        private PoolRow(final int index, final String name, final int bitCount, final String value){
            this.index = index;
            this.name = name;
            this.bitCount = bitCount;
            this.value = value;

            super.setOpaque(false);
            super.setPreferredSize(new Dimension(Integer.MAX_VALUE, IBCPoolSubEditor.ROW_HEIGHT));
            super.setMaximumSize(super.getPreferredSize());
            super.setMinimumSize(super.getPreferredSize());
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            g.setColor(IBCPoolSubEditor.LINE_COLOR);
            g.drawLine(0, 0, super.getWidth() - 1, 0); //north
            g.drawLine(super.getWidth() - 1, 0, super.getWidth() - 1, super.getHeight() - 1); //east
            g.drawLine(0, super.getHeight() - 1, super.getWidth() - 1, super.getHeight() - 1); //south
            g.drawLine(0, 0, 0, super.getHeight() - 1); //west

            int xOffset = 1; //start at 1 because of border
            g.drawLine(xOffset + IBCPoolSubEditor.this.indexWidth, 0, xOffset + IBCPoolSubEditor.this.indexWidth, super.getHeight() - 1);
            xOffset += IBCPoolSubEditor.this.indexWidth + 1;
            g.drawLine(xOffset + IBCPoolSubEditor.NAME_WIDTH, 0, xOffset + IBCPoolSubEditor.NAME_WIDTH, super.getHeight() - 1);
            xOffset += IBCPoolSubEditor.NAME_WIDTH + 1;
            g.drawLine(xOffset +  + IBCPoolSubEditor.this.bitWidth, 0, xOffset +  + IBCPoolSubEditor.this.bitWidth, super.getHeight() - 1);

            g.setColor(IComponent.DEFAULT_FOREGROUND);
            g.setFont(IBCPoolSubEditor.this.getFont());
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final FontMetrics metrics = g.getFontMetrics();
            final int y = super.getHeight() / 2 + metrics.getHeight() / 4;

            xOffset = 1;
            g.drawString(String.valueOf(this.index), xOffset + IBCPoolSubEditor.this.indexWidth / 2 - metrics.stringWidth(String.valueOf(this.index)) / 2, y);
            xOffset += IBCPoolSubEditor.this.indexWidth + 1;
            g.drawString(this.name, xOffset + IBCPoolSubEditor.PADDING, y);
            xOffset += IBCPoolSubEditor.NAME_WIDTH + 1;
            g.drawString(String.valueOf(this.bitCount), xOffset +  + IBCPoolSubEditor.this.bitWidth/ 2 - metrics.stringWidth(String.valueOf(this.bitCount)) / 2, y);
            xOffset += IBCPoolSubEditor.this.bitWidth + 1;
            g.drawString(this.value, xOffset + IBCPoolSubEditor.PADDING, y);
        }
    }
}
