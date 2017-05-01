package net.jetensky.keyboard3djava.opencv;

import net.jetensky.keyboard3djava.util.FileSystemUtil;
import net.jetensky.keyboard3djava.util.opencv.OpencvLoaderHelper;
import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Freenect2Test {

    @Test
    public void testSetTiltAngle() throws InterruptedException {
        OpencvLoaderHelper.initOpenCV();
        int height = 100;
        int width = 100;
        Mat result = new Mat(height, width, CvType.CV_32SC3);
        int indexIn = 0;

        for(int y = 0; y < height; ++y) {
            int indexOut = y * width;

            for(int x = 0; x < width; ++indexOut) {
                result.put(y, x, 255,0,0);
                ++x;
            }
        }
        FileSystemUtil.writeTraceImage(result);
    }


}
