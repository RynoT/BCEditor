package ui.component.explorer;

import ui.component.IImagePanel;
import util.AssetManager;
import util.async.AsyncEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by Ryan Thomson on 21/12/2016.
 */
public class IFolderNode extends ITileNode implements Iterable<ITileNode> {

    private int folderCount = 0;
    private boolean collapsed = true;
    private IImagePanel expandPanel, folderPanel;
    private BufferedImage expandedIcon, collapsedIcon;

    private final List<ITileNode> children = new ArrayList<>();

    public IFolderNode(final String name, final IFolderNode parent) {
        super(name, parent, ITileNode.TILE_FOLDER_INSET);

        assert (this.expandPanel != null && this.folderPanel != null);

        // Load expanded icon
        AssetManager.loadImage(AssetManager.EXPANDED_ICON, new AsyncEvent<BufferedImage>() {
            @Override
            public void onComplete(final BufferedImage item) {
                IFolderNode.this.expandedIcon = item;
                if(!IFolderNode.this.collapsed) {
                    IFolderNode.this.expandPanel.setImage(item);
                }
            }
        });
        // Load collapsed icon
        AssetManager.loadImage(AssetManager.COLLAPSED_ICON, new AsyncEvent<BufferedImage>() {
            @Override
            public void onComplete(final BufferedImage item) {
                IFolderNode.this.collapsedIcon = item;
                if(IFolderNode.this.collapsed) {
                    IFolderNode.this.expandPanel.setImage(item);
                }
            }
        });
        // Load folder icon
        AssetManager.loadImage(AssetManager.FOLDER_ICON, new AsyncEvent<BufferedImage>() {
            @Override
            public void onComplete(final BufferedImage item) {
                IFolderNode.this.folderPanel.setImage(item);
            }
        });
    }

    public boolean isCollapsed() {
        return this.collapsed;
    }

    public List<ITileNode> getChildren() {
        return this.children;
    }

    @Override
    public Iterator<ITileNode> iterator() {
        return new FileIterator(); // iterates through all children files of this folder (including those from child folders)
    }

    public void addChild(final ITileNode node) {
        if(node instanceof IFolderNode) {
            this.children.add(this.folderCount++, node);
        } else {
            this.children.add(node);
        }
    }

    @Override
    public void init() {
        final IImagePanel expandPanel = new IImagePanel();
        {
            expandPanel.setPreferredSize(new Dimension(ITileNode.TILE_SIZE, ITileNode.TILE_SIZE));
            expandPanel.setMaximumSize(expandPanel.getPreferredSize());
            expandPanel.setMinimumSize(expandPanel.getPreferredSize());

            expandPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent e) {
                    IFolderNode.this.onAction();
                }
            });
        }
        this.expandPanel = expandPanel;
        super.add(this.expandPanel);

        final IImagePanel folderPanel = new IImagePanel();
        {
            folderPanel.setPreferredSize(new Dimension(ITileNode.TILE_SIZE, ITileNode.TILE_SIZE));
            folderPanel.setMaximumSize(expandPanel.getPreferredSize());
            folderPanel.setMinimumSize(expandPanel.getPreferredSize());
        }
        this.folderPanel = folderPanel;
        super.add(folderPanel);
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onAction() {
        if(this.collapsed) {
            this.expand();
        } else {
            this.collapse();
        }
        super.getParent().revalidate();
        super.getParent().repaint();
    }

    public void expand() {
        if(!this.collapsed) {
            return; //already expanded
        }
        assert (SwingUtilities.isEventDispatchThread());

        this.collapsed = false; //this must be set before we try to expand
        this.expandSilent(super.getParent());
        this.expandPanel.setImage(this.expandedIcon);
    }

    public void collapse() {
        if(this.collapsed) {
            return; //already collapsed
        }
        assert (SwingUtilities.isEventDispatchThread());

        this.collapseSilent(super.getParent());
        this.collapsed = true; // this must be set after the collapsing is complete
        this.expandPanel.setImage(this.collapsedIcon);
    }

    // Expand this folder without modifying the state of this folder
    private void expandSilent(final Container container) {
        if(this.collapsed) {
            return;
        }
        assert (SwingUtilities.isEventDispatchThread());
        int index = container.getComponentZOrder(this);
        assert (index != -1);
        for(int i = 0; i < this.children.size(); i++) {
            final ITileNode node = this.children.get(i);
            container.add(node, index + i + 1);
            if(node instanceof IFileNode) {
                ((IFileNode) node).update();
            } else if(node instanceof IFolderNode) {
                final IFolderNode folder = (IFolderNode) node;
                if(!folder.isCollapsed()) {
                    folder.expandSilent(container);
                    index += folder.getChildren().size();
                }
            }
        }
    }

    // Collapse this folder without modifying the state of this folder
    private void collapseSilent(final Container container) {
        if(this.collapsed) {
            return;
        }
        assert (SwingUtilities.isEventDispatchThread());
        for(final ITileNode node : this.children) {
            if(node instanceof IFolderNode) {
                ((IFolderNode) node).collapseSilent(container);
            }
            container.remove(node);
        }
    }

    public IFolderNode findFolder(final String name) {
        for(int i = 0; i < this.folderCount; i++) {
            final ITileNode node = this.children.get(i);
            if(node.name.equals(name)) {
                return (IFolderNode) node;
            }
        }
        return null;
    }

    public void sort() {
        this.children.sort((node1, node2) -> {
            if(node2 instanceof IFolderNode) {
                // If our second node is not a folder
                if(!(node1 instanceof IFolderNode)) {
                    return 1;
                }
                // If our second node is also a folder, compare the names
            }
            return node1.name.compareToIgnoreCase(node2.name);
        });
    }

    private class FileIterator implements Iterator<ITileNode> {

        private final Stack<IteratorPointer> stack = new Stack<>();

        private FileIterator() {
            if(IFolderNode.this.children.size() > 0) {
                this.stack.push(new IteratorPointer(IFolderNode.this));
            }
        }

        @Override
        public boolean hasNext() {
            return this.stack.size() > 0;
        }

        @Override
        public ITileNode next() {
            if(this.stack.size() == 0){
                return null;
            }
            final IteratorPointer top = this.stack.peek();

            final ITileNode node = top.node.children.get(top.pointer++);
            if(top.node.children.size() <= top.pointer) {
                this.stack.pop();
            }
            if(node instanceof IFolderNode && ((IFolderNode) node).children.size() > 0) {
                this.stack.push(new IteratorPointer((IFolderNode) node));
                return this.next();
            }
            return node;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        private class IteratorPointer {

            private int pointer = 0;
            private final IFolderNode node;

            private IteratorPointer(final IFolderNode node) {
                this.node = node;
            }
        }
    }
}
