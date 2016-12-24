package ui.component;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Ryan Thomson on 21/12/2016.
 */
public class IImagePanel extends IComponent {

    private BufferedImage[] images;

    public IImagePanel(final BufferedImage... images){
        this.images = images == null ? new BufferedImage[0] : images;

        super.setOpaque(false);
    }

    public int getImageCount(){
        return this.images.length;
    }

    public BufferedImage[] getImages(){
        return this.images;
    }

    public void removeImages(){
        this.setImages();
    }

    public void setImage(final BufferedImage image){
        this.setImages(image);
    }

    public void setImages(final BufferedImage... images){
        this.images = images == null ? new BufferedImage[0] : images;
        super.repaint();
    }

    public void addImage(final BufferedImage image){
        final BufferedImage[] tmp = new BufferedImage[this.images.length + 1];
        System.arraycopy(this.images, 0, tmp, 0, this.images.length);
        tmp[tmp.length - 1] = image;
        this.images = tmp;

        super.repaint();
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        for(final BufferedImage image : this.images){
            if(image == null){
                continue;
            }
            g.drawImage(image, super.getWidth() / 2 - image.getWidth() / 2,
                    super.getHeight() / 2 - image.getHeight() / 2, null);
        }
    }
}
