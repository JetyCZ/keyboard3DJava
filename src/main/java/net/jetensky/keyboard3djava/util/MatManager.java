package net.jetensky.keyboard3djava.util;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;

public class MatManager extends ArrayList<Mat> {

    public void releaseMats() {
        for (Mat mat : this) {
            mat.release();
        }
        this.clear();
    }

    public Mat newMat() {
        Mat newMat = new Mat();
        add(newMat);
        return newMat;
    }

    /**
     * Creates new submat from source mat and registers it for later release
     */
    public Mat newSubMat(Mat sourceMat, Rect cutRegion) {
        Mat submat = sourceMat.submat(cutRegion);
        add(submat);
        return submat;
    }

    public Mat newClonedMat(Mat sourceMat) {
        Mat result = sourceMat.clone();
        add(sourceMat);
        return result;
    }
}
