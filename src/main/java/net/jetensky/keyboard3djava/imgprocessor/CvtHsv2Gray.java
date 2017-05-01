package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * As an input, takes hsv image, returns single channel image in gray
 */
public class CvtHsv2Gray extends AbstractImgProcessor {

    @Override
    public Img process(Img img) {

        Mat mat = img.getMat();
        List<Mat> channels = new ArrayList<>();
        Core.split(mat, channels);

        return img.from(channels.get(2).clone());
    }

}
