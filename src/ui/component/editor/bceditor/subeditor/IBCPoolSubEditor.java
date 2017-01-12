package ui.component.editor.bceditor.subeditor;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;
import ui.component.IBorder;
import ui.component.IComponent;
import ui.component.ILabel;
import ui.component.IScrollPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.stream.Stream;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class IBCPoolSubEditor extends IBCSubEditor {

    public static final int ROW_HEIGHT = 19;

    public static final int PADDING = 6;
    public static final int NAME_WIDTH = 100;

    public static final Color LINE_COLOR = new Color(63, 64, 65);

    public static final Color TABLE_FOCUSED_COLOR = IComponent.DEFAULT_BACKGROUND_HIGHLIGHT;
    public static final Color TABLE_SUCCESSOR_COLOR = new Color(57, 115, 47);
    public static final Color TABLE_PREDECESSOR_COLOR = new Color(40, 117, 113);

    private final JPanel tablePanel;

    private final ConstantPool pool;
    private int indexWidth = 0, bitWidth = 0;

    public IBCPoolSubEditor(final ConstantPool pool) {
        super("Constant Pool");

        assert pool != null;
        this.pool = pool;

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

    public void setActiveRow(final int index) {
        assert this.pool != null : "Pool Editor must be populated first";
        assert index >= 0 && index < this.tablePanel.getComponentCount() : "Invalid index";

        final PoolRow row = (PoolRow) this.tablePanel.getComponent(index);
        final PoolTag[] successors = this.pool.getSuccessors(row.tag), predecessors = this.pool.getPredecessors(row.tag);
        for(final Component component : this.tablePanel.getComponents()) {
            assert component instanceof PoolRow : "Table should only contain PoolRow's";

            final PoolRow next = (PoolRow) component;
            next.pressed = next.index == index;
            if(next.pressed) {
                next.setBackground(IBCPoolSubEditor.TABLE_FOCUSED_COLOR);
            } else {
                next.setBackground(IComponent.DEFAULT_BACKGROUND_INTERMEDIATE);
            }

            // Check to see if row is a successor or predecessor
            final boolean successor = Stream.of(successors).anyMatch(tag -> tag == next.tag);
            final boolean predecessor = Stream.of(predecessors).anyMatch(tag -> tag == next.tag);
            assert !(successor || predecessor) || successor != predecessor;

            if(successor || predecessor) {
                next.setBackground(successor ? IBCPoolSubEditor.TABLE_SUCCESSOR_COLOR : IBCPoolSubEditor.TABLE_PREDECESSOR_COLOR);
            }
            next.repaint();
        }
    }

    @Override
    public void populate() {
        if(this.tablePanel.getComponentCount() > 0){
            return; //already populated
        }
        final FontMetrics metrics = super.getFontMetrics(super.getFont());
        this.indexWidth = metrics.stringWidth(String.valueOf(this.pool
                .getEntryCount())) + IBCPoolSubEditor.PADDING * 2;

        assert SwingUtilities.isEventDispatchThread();
        this.tablePanel.removeAll();
        int maxRowWidth = 0;
        for(int i = 0; i < this.pool.getEntryCount(); i++) {
            final PoolRow row = new PoolRow(i, this.pool.getEntry(i));
            this.tablePanel.add(row);

            this.bitWidth = Math.max(this.bitWidth, metrics.stringWidth(String
                    .valueOf(row.bitCount)) + IBCPoolSubEditor.PADDING * 2);
            maxRowWidth = Math.max(maxRowWidth, metrics.stringWidth(row.content));
        }
        maxRowWidth += this.indexWidth + IBCPoolSubEditor.NAME_WIDTH + this.bitWidth + IBCPoolSubEditor.PADDING * 2;
        this.tablePanel.setPreferredSize(new Dimension(maxRowWidth, this.tablePanel.getComponentCount() * IBCPoolSubEditor.ROW_HEIGHT));

        this.tablePanel.revalidate();
        this.tablePanel.repaint();
    }

    private class PoolRow extends JPanel {

        private final PoolTag tag;

        private int index, bitCount;
        private String name, content;

        private boolean pressed = false;

        private PoolRow(final int index, final PoolTag tag) {
            this.tag = tag;
            this.updateRow(index);

            super.setBackground(IComponent.DEFAULT_BACKGROUND_INTERMEDIATE);
            super.setPreferredSize(new Dimension(Integer.MAX_VALUE, IBCPoolSubEditor.ROW_HEIGHT));
            super.setMaximumSize(super.getPreferredSize());
            super.setMinimumSize(super.getPreferredSize());

            super.addMouseListener(new MouseAdapter() {
                private Color lastColor = null;

                @Override
                public void mousePressed(final MouseEvent e) {
                    IBCPoolSubEditor.this.setActiveRow(PoolRow.this.index);
                    this.lastColor = PoolRow.this.getBackground();
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                    this.lastColor = PoolRow.this.getBackground();
                    PoolRow.this.setBackground(this.lastColor.darker());
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    assert this.lastColor != null;
                    PoolRow.this.setBackground(this.lastColor);
                }
            });
        }

        public void updateRow(final int newIndex){
            this.index = newIndex;
            this.name = this.tag.getPoolTagName();
            this.bitCount = this.tag.getPoolTagBitCount();
            this.content = this.tag.getContentString(IBCPoolSubEditor.this.pool);
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            assert this.tag != null;
            assert IBCPoolSubEditor.this.pool != null;

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
            g.drawLine(xOffset + IBCPoolSubEditor.this.bitWidth, 0, xOffset + +IBCPoolSubEditor.this.bitWidth, super.getHeight() - 1);

            g.setColor(IComponent.DEFAULT_FOREGROUND);
            g.setFont(IBCPoolSubEditor.this.getFont());
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final FontMetrics metrics = g.getFontMetrics();
            final int y = super.getHeight() / 2 + metrics.getHeight() / 2 - 2;

            xOffset = 1;
            g.drawString(String.valueOf(this.index), xOffset + IBCPoolSubEditor.this.indexWidth / 2 - metrics.stringWidth(String.valueOf(this.index)) / 2, y);
            xOffset += IBCPoolSubEditor.this.indexWidth + 1;
            g.drawString(this.name, xOffset + IBCPoolSubEditor.PADDING, y);
            xOffset += IBCPoolSubEditor.NAME_WIDTH + 1;
            g.drawString(String.valueOf(this.bitCount), xOffset + +IBCPoolSubEditor.this.bitWidth / 2 - metrics.stringWidth(String.valueOf(this.bitCount)) / 2, y);
            xOffset += IBCPoolSubEditor.this.bitWidth + 1;
            g.drawString(this.content, xOffset + IBCPoolSubEditor.PADDING, y);
        }
    }
}
