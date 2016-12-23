package ui.component.explorer;

import project.filetype.ClassType;
import project.filetype.FileType;
import project.filetype.ImageType;
import project.filetype.TextType;
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

    public void updateIcon() {
        if(this.iconPanel.getImage() != null) {
            return; //image is already loaded so we won't do it again
        }
        // Load icon according to file type
        final String iconAsset;
        if(this.file instanceof ClassType) {
            iconAsset = AssetManager.CLASS_ICON;
        } else if(this.file instanceof TextType) {
            iconAsset = AssetManager.TEXT_ICON;
        } else if(this.file instanceof ImageType) {
            iconAsset = AssetManager.IMAGE_ICON;

            // Load in an image and resize it to an icon, and set that icon
            Async.submit(() -> {
                final BufferedImage in;
                try (final InputStream is = IFileNode.this.file.getStream()){
                    in = ImageIO.read(is);
                } catch(final IOException e) {
                    e.printStackTrace(System.err);
                    return;
                }
                assert (in != null);

                float width = IFileNode.TILE_IMAGE_SIZE, height = IFileNode.TILE_IMAGE_SIZE;
                final float aspect = (float)Math.min(in.getHeight(), in.getWidth()) / (float)Math.max(in.getWidth(), in.getHeight());
                if(in.getWidth() < in.getHeight()){
                    height *= aspect;
                } else {
                    width *= aspect;
                }
                final BufferedImage out = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D g2d = out.createGraphics();
                {
                    g2d.drawImage(in, 0, 0, out.getWidth(), out.getHeight(), null);
                }
                g2d.dispose();
                synchronized(IFileNode.class) {
                    IFileNode.this.iconPanel.setImage(out);
                }
            }, AsyncType.MULTI);
        } else {
            // for unknown/binary types
            iconAsset = AssetManager.UNKNOWN_ICON;
        }
        //if(iconAsset == null){
        //    System.err.println("[IFileNode] No icon for file-type: " + this.file.getTagName() + "." + this.file.getExtension());
        //}
        AssetManager.loadImage(iconAsset, new AsyncEvent<BufferedImage>() {
            @Override
            public void onComplete(final BufferedImage image) {
                synchronized(IFileNode.class) {
                    if(IFileNode.this.iconPanel.getImage() != null) {
                        return; //ignore image changes
                    }
                    IFileNode.this.iconPanel.setImage(image);
                }
            }
        });
    }
}
