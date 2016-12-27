package ui.component.explorer;

import project.filetype.ClassType;
import project.filetype.FileType;
import project.filetype.ImageType;
import project.filetype.TextType;
import project.filetype.classtype.index.Index;
import ui.Canvas;
import ui.component.IFileViewer;
import ui.component.IImagePanel;
import ui.component.ILabel;
import ui.component.ITextAlign;
import util.AssetManager;
import util.async.Async;
import util.async.AsyncEvent;
import util.async.AsyncType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ryan Thomson on 21/12/2016.
 */
public class IFileNode extends ITileNode {

    public static final int TILE_IMAGE_SIZE = ITileNode.TILE_SIZE - 4;

    public static final int TILE_COMMENT_PADDING = 6;
    public static final Color TILE_COMMENT_COLOR = new Color(160, 160, 160);
    public static final Color TILE_COMMENT_FOCUS_COLOR = new Color(167, 244, 6);

    private final FileType file;

    private ILabel comment = null;
    private IImagePanel iconPanel;

    private final Object iconSyncLock = new Object();

    public IFileNode(final FileType file, final String name, final IFolderNode parent) {
        super(name, parent, ITileNode.TILE_FILE_INSET);

        assert (this.iconPanel != null);

        assert (file != null);
        this.file = file;
    }

    public ILabel getComment(){
        return this.comment;
    }

    public FileType getFileType(){
        return this.file;
    }

    public IImagePanel getIconPanel(){
        return this.iconPanel;
    }

    @Override
    public void onAction() {
        Canvas.getFileViewer().open(this);
    }

    @Override
    public void init() {
        final IImagePanel iconPanel = new IImagePanel();
        {
            iconPanel.setPreferredSize(new Dimension(ITileNode.TILE_SIZE, ITileNode.TILE_SIZE));
            iconPanel.setMaximumSize(iconPanel.getPreferredSize());
            iconPanel.setMinimumSize(iconPanel.getPreferredSize());
        }
        this.iconPanel = iconPanel;
        super.add(this.iconPanel);
    }

    @Override
    public void onFocus() {
        for(final ITileNode next : Canvas.getProjectExplorer().getRootNode()) {
            if(next == null) {
                continue;
            }
            assert (next instanceof IFileNode);
            final IFileNode node = (IFileNode) next;
            if(node.comment == null) {
                continue;
            }
            if(this.comment == null) {
                node.comment.setColor(IFileNode.TILE_COMMENT_COLOR);
            } else if(this.comment.getText().equals(node.comment.getText())) {
                node.comment.setColor(IFileNode.TILE_COMMENT_FOCUS_COLOR);
            } else {
                node.comment.setColor(IFileNode.TILE_COMMENT_COLOR);
            }
        }
    }

    private void setIcon(final String path) {
        AssetManager.loadImage(path, new AsyncEvent<BufferedImage>() {
            @Override
            public void onComplete(final BufferedImage image) {
                synchronized(IFileNode.this.iconSyncLock) {
                    IFileNode.this.iconPanel.setImageCount(1);
                    IFileNode.this.iconPanel.setImage(0, image);
                    IFileNode.this.updateTabIcon();
                }
            }
        });
    }

    private void setIconImage(final String path, final int index) {
        AssetManager.loadImage(path, new AsyncEvent<BufferedImage>() {
            @Override
            public void onComplete(final BufferedImage image) {
                synchronized(IFileNode.this.iconSyncLock) {
                    IFileNode.this.iconPanel.setImage(index, image);
                    IFileNode.this.updateTabIcon();
                }
            }
        });
    }

    public void setComment(String comment) {
        if(comment == null || comment.trim().equals("")) {
            if(this.comment != null) {
                super.remove(this.comment);
                super.repaint();
                this.comment = null;
            }
            return;
        }
        comment = "[" + comment + "]";
        if(this.comment == null) {
            final ILabel label = new ILabel(comment);
            {
                label.setAlignment(ITextAlign.LEFT);
                label.setColor(IFileNode.TILE_COMMENT_COLOR);
                label.setPadding(0, 0, 0, IFileNode.TILE_COMMENT_PADDING);
            }
            this.comment = label;
            super.add(label);
            super.revalidate();
        } else {
            this.comment.setText(comment);
        }
        this.comment.repaint();
    }

    private void setImageIcon() {
        // Load in an image and resize it to an icon, and set that icon
        final BufferedImage in;
        try(final InputStream is = this.file.getStream()) {
            in = ImageIO.read(is);
        } catch(final IOException e) {
            e.printStackTrace(System.err);
            return;
        }
        assert (in != null);

        float width = IFileNode.TILE_IMAGE_SIZE, height = IFileNode.TILE_IMAGE_SIZE;
        final float aspect = (float) Math.min(in.getHeight(), in.getWidth()) / (float) Math.max(in.getWidth(), in.getHeight());
        if(in.getWidth() < in.getHeight()) {
            height *= aspect;
        } else {
            width *= aspect;
        }
        final BufferedImage out = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = out.createGraphics();
        {
            g2d.drawImage(in, 0, 0, out.getWidth(), out.getHeight(), null);
        }
        g2d.dispose();
        synchronized(this.iconSyncLock) {
            this.iconPanel.addImage(out);
            this.updateTabIcon();
        }
    }

    private void setClassIcon() {
        final ClassType clazz = (ClassType) this.file;
        if(!clazz.isIndexed()) {
            return;
        }
        final Index index = clazz.getIndex();
        assert (index != null);

        if(index.isEnum()) {
            this.setIcon(AssetManager.ENUM_ICON);
        } else if(index.isInterface()) {
            this.setIcon(AssetManager.INTERFACE_ICON);
        } else {
            synchronized(this.iconSyncLock) {
                this.iconPanel.setImageCount(3);
            }
            this.setIconImage(AssetManager.CLASS_ICON, 0);
            if(index.isMainClass()) {
                this.setIconImage(AssetManager.MAIN_MOD_ICON, 1);
            }
            if(index.isFinal()) {
                this.setIconImage(AssetManager.FINAL_MOD_ICON, 2);
            }
            if(index.isAbstract()) {
                this.setIconImage(AssetManager.ABSTRACT_MOD_ICON, 2);
            }
        }
    }

    public void update() {
        synchronized(this.iconSyncLock) {
            this.iconPanel.setImageCount(0);
        }
        this.updateIcon();
        this.updateComment();
    }

    private void updateTabIcon(){
        for(final IFileViewer.ViewerTab tab : Canvas.getFileViewer().getTabs()){
            if(tab.getNode() == this){
                tab.updateButton();
                break;
            }
        }
    }

    public void updateComment() {
        if(this.file instanceof ClassType) {
            final ClassType clazz = (ClassType) this.file;
            if(clazz.isIndexed()) {
                final String superclass = clazz.getIndex().getSuperClass();
                if(superclass.equals("java.lang.Object") || (this.comment != null
                        && this.comment.getText().equals("[" + superclass + "]"))) {
                    return;
                }
                this.setComment(superclass);
            }
        }
    }

    public void updateIcon() {
        synchronized(this.iconSyncLock) {
            // We don't need to set an image if one has already been set.
            if(this.iconPanel.getImageCount() != 0) {
                return;
            }
        }
        // Load icon according to file type
        final String iconAsset;
        if(this.file instanceof ClassType) {
            //iconAsset = AssetManager.CLASS_ICON;

            // Load class icon
            Async.submit(this::setClassIcon, AsyncType.MULTI);
            return;
        } else if(this.file instanceof ImageType) {
            //iconAsset = AssetManager.IMAGE_ICON;

            // Load image into icon
            Async.submit(this::setImageIcon, AsyncType.MULTI);
            return;
        } else if(this.file instanceof TextType) {
            iconAsset = AssetManager.TEXT_ICON;
        } else {
            iconAsset = AssetManager.UNKNOWN_ICON;
        }
        this.setIcon(iconAsset);
    }
}
