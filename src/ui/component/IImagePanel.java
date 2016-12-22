package ui.component;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Ryan Thomson on 21/12/2016.
 */
public class IImagePanel extends IComponent {

    private BufferedImage image;

    public IImagePanel(){
        this(null);
    }

    public IImagePanel(final BufferedImage image){
        this.image = image;

        super.setOpaque(false);
    }

    public BufferedImage getImage(){
        return this.image;
    }

    public void setImage(final BufferedImage image){
        this.image = image;
        super.repaint();
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if(this.image != null) {
            g.drawImage(this.image, super.getWidth() / 2 - this.image.getWidth() / 2,
                    super.getHeight() / 2 - this.image.getHeight() / 2, null);
        }
    }
}
