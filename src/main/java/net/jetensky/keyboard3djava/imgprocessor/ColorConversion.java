package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * As an input, takes hsv image, returns single channel image in gray
 */
public class ColorConversion extends AbstractImgProcessor {

    private int convertCode;

    /**
     *
     * @param convertCode - one ov Imgproc.COLOR_XXX2YYY constants
     */
    public ColorConversion(int convertCode) {
        this.convertCode = convertCode;
    }

    @Override
    public Img process(Img img) {

        Mat mat = img.getMat();
        Mat rgb = new Mat();
        Imgproc.cvtColor(mat, rgb, convertCode);

        return img.from(rgb);
    }

}
