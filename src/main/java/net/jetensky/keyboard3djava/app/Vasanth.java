package net.jetensky.keyboard3djava.app;

import net.jetensky.keyboard3djava.util.FileSystemUtil;
import net.jetensky.keyboard3djava.util.opencv.OpencvLoaderHelper;
import net.jetensky.keyboard3djava.util.swing.SwingUtils;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Vasanth {

    public static void main(String[] args) throws InterruptedException {
        OpencvLoaderHelper.initOpenCV();

        Mat frame = FileSystemUtil.readImage("/tmp/debug/a2.png");
        BufferedImage bufferedImage = SwingUtils.matToBufferedImage(frame);
        showWindow(bufferedImage);
    }

    private static void showWindow(BufferedImage img) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.setSize(img.getWidth(), img.getHeight() + 30);
        frame.setTitle("Image captured");
        frame.setVisible(true);
    }
}