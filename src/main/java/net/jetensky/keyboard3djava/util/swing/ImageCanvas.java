package net.jetensky.keyboard3djava.util.swing;

import java.awt.image.BufferedImage;

public class ImageCanvas {
    private BufferedImage image;

    public ImageCanvas(BufferedImage image) {

        this.image = image;
    }

    public void drawVerticalLine(int minX) {
            for(int y = 0; y < image.getHeight(); ++y) {
                image.setRGB(minX, y, toColor(255,0,0));
            }
    }

    public static int toColor(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public void drawHorizontalLine(int minY, int color) {
        try {
            for(int x = 0; x < image.getWidth(); ++x) {
                image.setRGB(x, minY, color);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}


