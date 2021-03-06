package net.jetensky.keyboard3djava.util;

import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

/**
 * This is not class to be used in production, but during development phase while debugging image processing
 */
public class UIUtil {
    public static void showWindow(Mat mat) {
        BufferedImage bufferedImage = matToBufferedImage(mat);
        FileSystemUtil.writeTraceImage(mat);
        showWindow(bufferedImage);
    }

    public static BufferedImage matToBufferedImage(Mat frame) {
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }

    public static void showWindow(BufferedImage img)  {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.setSize(img.getWidth(), img.getHeight() + 30);
        frame.setTitle("Image " + img.getWidth() + "x" + img.getHeight() + ", type=" + img.getType());
        frame.setVisible(true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
    }
}
