package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import net.jetensky.keyboard3djava.util.ImageUtil;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Erode extends AbstractImgProcessor {

    private int erosionKernelSize;
    private int structuringElemShape;

    public Erode(int erosionKernelSize) {
        this.erosionKernelSize = erosionKernelSize;
    }

    @Override
    public Img process(Img img) {
        setToProcess(img.getMat());
        structuringElemShape = Imgproc.MORPH_RECT;
        Mat eroded = ImageUtil.erode(toProcess, erosionKernelSize, structuringElemShape);
        return img.from(eroded);
    }

    /**
     * Use one of the constants Imgproc.MORPH_
     * @param structuringElemShape
     */
    public void setStructuringElemShape(int structuringElemShape) {
        this.structuringElemShape = structuringElemShape;
    }
}
