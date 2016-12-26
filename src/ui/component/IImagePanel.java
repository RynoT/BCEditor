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

    public IImagePanel(final IImagePanel panel){
        this.images = panel.images;
        super.setOpaque(panel.isOpaque());
    }

    public int getImageCount(){
        return this.images.length;
    }

    public BufferedImage[] getImages(){
        return this.images;
    }

    public void setImageCount(final int count){
        if(this.images.length == count){
            return;
        }
        this.images = new BufferedImage[count];
        super.repaint();
    }

    public void setImage(final int index, final BufferedImage image){
        assert(index >= 0 && index < this.images.length);
        this.images[index] = image;
        super.repaint();
    }

    public void setImage(final BufferedImage image){
        if(this.images.length != 1){
            this.setImageCount(1);
        }
        this.images[0] = image;
        super.repaint();
    }

    public void addImage(final BufferedImage image){
        for(int i = 0; i < this.images.length; i++){
            if(this.images[i] != null){
                continue;
            }
            this.images[i] = image;
            super.repaint();
            return;
        }
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
