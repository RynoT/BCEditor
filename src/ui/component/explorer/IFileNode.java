package ui.component.explorer;

import project.filetype.ClassType;
import project.filetype.FileType;
import project.filetype.ImageType;
import project.filetype.TextType;
import project.filetype.classtype.index.Index;
import ui.Canvas;
import ui.component.IImagePanel;
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

    private final FileType file;

    private IImagePanel iconPanel;
    private final Object iconSyncLock = new Object();

    public IFileNode(final FileType file, final String name, final IFolderNode parent) {
        super(name, parent, ITileNode.TILE_FILE_INSET);

        assert (this.iconPanel != null);

        assert (file != null);
        this.file = file;
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
    public void action() {
        Canvas.getCanvas().open(this.file);
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
            this.iconPanel.setImage(out);
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
            if(index.isMainClass()) {
                this.addToIcon(AssetManager.MAIN_MOD_ICON);
            }
            if(index.isFinal()) {
                this.addToIcon(AssetManager.FINAL_MOD_ICON);
            }
            if(index.isAbstract()) {
                this.addToIcon(AssetManager.ABSTRACT_MOD_ICON);
            }
        }


//            if(index.isEnum()) {
//                this.iconPanel.setImage(AssetManager.loadImage(AssetManager.ENUM_ICON));
//            } else if(index.isInterface()) {
//                this.iconPanel.setImage(AssetManager.loadImage(AssetManager.INTERFACE_ICON));
//            } else {
//                final boolean isMain = index.isMainClass(), isFinal = index.isFinal(), isAbstract = index.isAbstract();
//                if(!isMain && !isFinal && !isAbstract){
//                    return;
//                }
//                String asset = AssetManager.CLASS_ICON;
//                if(isMain){
//                    asset += AssetManager.MAIN_MOD_ICON;
//                }
//                if(isFinal){
//                    asset += AssetManager.FINAL_MOD_ICON;
//                }
//                if(isAbstract){
//                    asset += AssetManager.ABSTRACT_MOD_ICON;
//                }
//                if(AssetManager.containsImage(asset)){
//                    this.iconPanel.setImage(AssetManager.loadImage(asset));
//                    return;
//                }
//                final BufferedImage icon = new BufferedImage(ITileNode.TILE_SIZE,
//                        ITileNode.TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
//                final Graphics2D g2d = icon.createGraphics();
//                {
//                    g2d.drawImage(AssetManager.loadImage(AssetManager.CLASS_ICON), 0, 0, null);
//
//                    if(isMain){
//                        g2d.drawImage(AssetManager.loadImage(AssetManager.MAIN_MOD_ICON), 0, 0, null);
//                    }
//                    if(isFinal){
//                        g2d.drawImage(AssetManager.loadImage(AssetManager.FINAL_MOD_ICON), 0, 0, null);
//                    }
//                    if(isAbstract){
//                        g2d.drawImage(AssetManager.loadImage(AssetManager.ABSTRACT_MOD_ICON), 0, 0, null);
//                    }
//                }
//                g2d.dispose();
//
//                AssetManager.addImage(asset, icon);
//                this.iconPanel.setImage(icon);
//            }
//        }
    }

    public void resetIcon(){
        synchronized(this.iconSyncLock){
            this.iconPanel.removeImages();
        }
    }

    private void setIcon(final String path) {
        AssetManager.loadImage(path, new AsyncEvent<BufferedImage>() {
            @Override
            public void onComplete(final BufferedImage image) {
                synchronized(IFileNode.this.iconSyncLock) {
                    IFileNode.this.iconPanel.setImage(image);
                }
            }
        });
    }

    private void addToIcon(final String path) {
        AssetManager.loadImage(path, new AsyncEvent<BufferedImage>() {
            @Override
            public void onComplete(final BufferedImage image) {
                synchronized(IFileNode.this.iconSyncLock) {
                    IFileNode.this.iconPanel.addImage(image);
                }
            }
        });
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
            iconAsset = AssetManager.CLASS_ICON;
            Async.submit(this::setClassIcon, AsyncType.MULTI);
        } else if(this.file instanceof TextType) {
            iconAsset = AssetManager.TEXT_ICON;
        } else if(this.file instanceof ImageType) {
            iconAsset = AssetManager.IMAGE_ICON;
            Async.submit(this::setImageIcon, AsyncType.MULTI);
        } else {
            // for undefined/binary types
            iconAsset = AssetManager.UNKNOWN_ICON;
        }
        AssetManager.loadImage(iconAsset, new AsyncEvent<BufferedImage>() {
            @Override
            public void onComplete(final BufferedImage image) {
                synchronized(IFileNode.this.iconSyncLock) {
                    IFileNode.this.iconPanel.setImage(image);
                }
            }
        });
    }
}
