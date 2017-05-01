package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import net.jetensky.keyboard3djava.util.ImageUtil;
import org.opencv.core.Mat;

public class Dilate extends AbstractImgProcessor {

    private int erosionKernelSize;

    public Dilate(int erosionKernelSize) {
        this.erosionKernelSize = erosionKernelSize;
    }

    @Override
    public Img process(Img img) {
        setToProcess(img.getMat());
        Mat dilated = ImageUtil.dilate(toProcess, erosionKernelSize, erosionKernelSize);
        return img.from(dilated);
    }


}
