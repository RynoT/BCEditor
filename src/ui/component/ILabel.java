package ui.component;

import util.async.Async;
import util.async.AsyncEvent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.AttributedString;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class ILabel extends IComponent {

    public static final int ICON_TEXT_PADDING = 1;
    public static final int TEXT_HEIGHT_PADDING = 8;

    private String text;
    private int mnemonic = -1;

    private boolean hasIcon, updateText = false;
    private BufferedImage icon;
    private BufferedImage textRender;

    public ILabel(final String text){
        this(text, -1);
    }

    public ILabel(final String text, final int mnemonic){
        this(text, mnemonic, null);
    }

    public ILabel(final String text, final BufferedImage icon){
        this(text, -1, icon);
    }

    public ILabel(final String text, final int mnemonic, final BufferedImage icon){
        this.text = text;
        this.mnemonic = mnemonic;
        this.icon = icon;
        this.hasIcon = icon != null;
        this.textRender = this.generateTextRender();

        super.setBackground(Color.PINK);
        //super.addMouseListener(new IForwardMouseEvent()); //we only need this if we make labels clickable, which right now we're not
    }

    public String getText(){
        return this.text;
    }

    public BufferedImage getIcon(){
        return this.icon;
    }

    public int getLabelWidth(){
        return !this.hasIcon ? this.textRender.getWidth() : (this.textRender.getWidth() + ILabel.ICON_TEXT_PADDING + this.textRender.getWidth());
    }

    public int getLabelHeight(){
        return this.textRender.getHeight() + ILabel.TEXT_HEIGHT_PADDING;
    }

    void setMnemonic(final int key){
        this.mnemonic = key;
        this.updateText = true;
        this.repaint();
    }

    public void setText(final String text){
        this.text = text;
        this.updateText = true;
        super.repaint();
    }

    public void setIcon(final BufferedImage icon){
        assert (icon == null || icon.getWidth() == icon.getHeight());
        this.icon = icon;
        this.hasIcon = icon != null;
        super.updateDimensions();
        super.repaint();
    }

    // This method will load the icon async
    public void setIcon(final File file){
        assert (file.exists());
        // assume we are going to successfully load the icon
        this.hasIcon = true;
        Async.loadImage(file, new AsyncEvent<BufferedImage>() {
            private final BufferedImage cache = ILabel.this.icon;

            @Override
            public void onComplete(final BufferedImage item) {
                if(this.cache != ILabel.this.icon){
                    // the icon was changed while this async task was happening
                    // we're just going to stop this task from moving forward
                    return;
                }
                if(this.cache != null && item == null){
                    // we can assume that the load failed; cancel operation
                    return;
                }
                ILabel.this.setIcon(item);
            }
        });
    }

    @Override
    protected void setDimensions() {
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if(this.updateText) {
            this.textRender = this.generateTextRender();
            super.updateDimensions();

            assert (this.textRender != null);
        }

        final Graphics2D g2d = (Graphics2D) g;
        final int y = super.getHeight() / 2 - this.textRender.getHeight() / 2;
        if(!this.hasIcon) {
            // if we only have to render the text
            g2d.drawImage(this.textRender, super.getWidth() / 2 - this.textRender.getWidth() / 2, y, null);
        } else {
            // the icon should be square and have the similar height as the text
            final int totalWidth = this.icon.getWidth() + ILabel.ICON_TEXT_PADDING + this.textRender.getWidth();
            if(this.icon != null) {
                g2d.drawImage(this.icon, super.getWidth() / 2 - totalWidth / 2, super.getHeight() / 2 - this.icon.getHeight() / 2 + 1, null);
            }
            g2d.drawImage(this.textRender, super.getWidth() / 2 - totalWidth / 2 + this.icon.getWidth() + ILabel.ICON_TEXT_PADDING, y, null);
        }
    }

    private BufferedImage generateTextRender(){
        final FontMetrics metrics = super.getFontMetrics(super.getFont());
        final Rectangle2D bounds = metrics.getStringBounds(this.text, super.getGraphics());
        final BufferedImage image = new BufferedImage(Math.round((float)bounds.getWidth()),
                Math.round((float)bounds.getHeight()), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = image.createGraphics();
        {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //g2d.setColor(Color.ORANGE);
            //g2d.fillRect(0, 0, 100, 100);
            g2d.setColor(Color.BLACK);
            //g2d.setFont(super.getFont());
            //g2d.drawString(this.text, 0, image.getHeight() - 2);

            final AttributedString as = new AttributedString(this.text);
            as.addAttribute(TextAttribute.FONT, super.getFont());
            if(this.mnemonic != -1){
                int index = -1;
                final String key = KeyEvent.getKeyText(this.mnemonic);
                for(int i = 0; i < this.text.length(); i++){
                    if(String.valueOf(this.text.charAt(i)).equalsIgnoreCase(key)){
                        index = i;
                        break;
                    }
                }
                if(index != -1){
                    as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, index, index + 1);
                }
            }
            g2d.drawString(as.getIterator(), 0, image.getHeight() - 2);
        }
        g2d.dispose();

        return image;
    }
}
