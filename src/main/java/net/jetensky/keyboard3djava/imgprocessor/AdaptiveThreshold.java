package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * As an input, takes hsv image, returns single channel image in gray
 */
public class AdaptiveThreshold extends AbstractImgProcessor {

    private int blockSize = 101;

    private int adaptiveMethod = Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
    @Override
    public Img process(Img img) {

        Mat mat = img.getMat();
        Mat adaptiveThreshold = new Mat();
        int maxValue = 255;

        Imgproc.adaptiveThreshold(mat, adaptiveThreshold, maxValue,
                adaptiveMethod, Imgproc.THRESH_BINARY, blockSize, 4);

        return img.from(adaptiveThreshold);
    }


    public AdaptiveThreshold setBlockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    public AdaptiveThreshold setAdaptiveMethod(int adaptiveMethod) {
        this.adaptiveMethod = adaptiveMethod;
        return this;
    }
}
