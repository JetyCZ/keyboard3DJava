package net.jetensky.keyboard3djava.util;

import org.opencv.core.Mat;

import java.util.ArrayList;

public class MatExecutor extends ArrayList<Mat> {

    Mat mat;
    private Mat cloneMatSource;


    public MatExecutor() {

    }

    public MatExecutor(Mat cloneMatSource) {
        this.cloneMatSource = cloneMatSource;
    }

    public Mat getMat() {
        if (mat==null) throw new IllegalStateException("getMat can be called only inside execute method from runnable");
        return mat;
    }

    public void execute(Runnable runnable) {
        MatManager matManager = new MatManager();
        try {
            if (cloneMatSource == null) {
                mat = matManager.newMat();
            } else {
                mat = cloneMatSource.clone();
                matManager.add(mat);
            }
            runnable.run();
        } finally {
            if (matManager!=null) {
                matManager.releaseMats();
            }
            cloneMatSource = null;
        }
    }
}
