package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import net.jetensky.keyboard3djava.util.ImageUtil;
import org.opencv.core.Mat;

public class Rotate extends AbstractImgProcessor{
    private int angle;

    public Rotate(int angle) {
        this.angle = angle;
    }

    @Override
    public Img process(Img img) {
        if (angle==0) return img;
        Mat mat = ImageUtil.rotateByAngle(angle, img.getMat());
        return img.from(mat);
    }
}
