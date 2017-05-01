package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * As an input, takes hsv image, returns single channel image in gray
 */
public class GaussianBlur extends AbstractImgProcessor {

    private Size kernelSize = new Size(3,3);

    @Override
    public Img process(Img img) {

        Mat mat = img.getMat();
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(mat, blurred, kernelSize, 1);

        return img.from(blurred);
    }

    public GaussianBlur setKernelSize(Size kernelSize) {
        this.kernelSize = kernelSize;
        return this;
    }
    public GaussianBlur setKernelSize(int kernelSize) {
        this.kernelSize = new Size(kernelSize,kernelSize);
        return this;
    }
}
