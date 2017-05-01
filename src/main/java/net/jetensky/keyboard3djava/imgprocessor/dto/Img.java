package net.jetensky.keyboard3djava.imgprocessor.dto;

import org.opencv.core.Mat;

public class Img {
    private Mat mat;
    private String folder;
    private boolean singleChannel;

    public Img(String folder, Mat mat) {
        this.mat = mat;
        this.folder = folder;
    }

    public Mat getMat() {
        return mat;
    }

    public Img from(Mat mat) {
        return new Img(this.folder, mat);
    }

    public void setSingleChannel(boolean singleChannel) {
        this.singleChannel = singleChannel;
    }

    public boolean isSingleChannel() {
        return singleChannel;
    }

    public String getFolder() {
        return folder;
    }
}
