package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * As an input, takes binary image with scanned document (black letters, lines and borders)
 * Outputs same image with preserved letters, but removed bigger arreas of black (background, images, logos, big fonts)
 * Can be used also to remove black big rectangles and keep Matrix and QR codes
 */
public class RemoveThickBlackPatches extends AbstractImgProcessor {


    private double pageWidthFraction = (double) 1/30;;
    private String name;

    public RemoveThickBlackPatches(String name) {
        this.name = name;
    }

    @Override
    public Img process(Img img) {
        ImgProcessorPipeline pipeline = new ImgProcessorPipeline(name);
        pipeline.add(new Inversion());
        Erode erode1 = new Erode((int) (pageWidthFraction * img.getMat().width()));
        pipeline.add(erode1);
        pipeline.add(new Dilate((int) (pageWidthFraction * img.getMat().width() + 5)));
        Img dilated = pipeline.process(img);

        Mat withoutBigPatches = img.getMat().clone();
        withoutBigPatches.setTo(new Scalar(255), dilated.getMat());

        pipeline.release(true);
        return img.from(withoutBigPatches);
    }


    public void setPageWidthFraction(double pageWidthFraction) {
        this.pageWidthFraction = pageWidthFraction;
    }
}
