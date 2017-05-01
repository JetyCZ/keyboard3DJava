package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * As an input, takes rgb image, returns binary image with
 * Returns HSV output, where if original pixel contained color, its color is set to white,
 * other pixels are untouched
 */
public class Paper extends AbstractImgProcessor {

    @Override
    public Img process(Img img) {

        Mat source = img.getMat();
        int kernelSize = 9;
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Mat destination = new Mat(source.rows(),source.cols(),source.type());
        Mat kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
            {
                put(0,0,-1);
                put(0,1,0);
                put(0,2,1);

                put(1,0-2);
                put(1,1,0);
                put(1,2,2);

                put(2,0,-1);
                put(2,1,0);
                put(2,2,1);
            }
        };
        Imgproc.filter2D(source, destination, -1, kernel);

        return img.from(destination);
    }

}
