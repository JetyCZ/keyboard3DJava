package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * As an input, takes hsv image, returns single channel image in gray
 */
public class CvtHsv2Rgb extends AbstractImgProcessor {

    @Override
    public Img process(Img img) {

        Mat mat = img.getMat();
        Mat rgb = new Mat();
        Imgproc.cvtColor(mat, rgb, Imgproc.COLOR_HSV2RGB);

        return img.from(rgb);
    }

}
