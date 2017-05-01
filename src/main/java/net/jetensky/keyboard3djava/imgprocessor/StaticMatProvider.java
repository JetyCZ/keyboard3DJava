package net.jetensky.keyboard3djava.imgprocessor;

import org.opencv.core.Mat;

public class StaticMatProvider implements MatProvider {


    private Mat matOutput;

    public StaticMatProvider(Mat matOutput) {
        this.matOutput = matOutput;
    }

    @Override
    public Mat matOutput() {
        return matOutput;
    }

    @Override
    public Mat toProcess() {
        throw new IllegalArgumentException("Method is not supported in this class");
    }
}
