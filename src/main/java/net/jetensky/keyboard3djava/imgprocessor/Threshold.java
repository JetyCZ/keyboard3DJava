package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Threshold extends AbstractImgProcessor {

    private double threshold;

    @Override
    public Img process(Img img) {
        setToProcess(img.getMat());

        Mat thresholded = new Mat();
        Imgproc.threshold(toProcess,thresholded, threshold, 255, Imgproc.THRESH_BINARY)        ;

        return img.from(thresholded);
    }

    public Threshold setThreshold(double threshold) {
        this.threshold = threshold;
        return this;
    }
}
