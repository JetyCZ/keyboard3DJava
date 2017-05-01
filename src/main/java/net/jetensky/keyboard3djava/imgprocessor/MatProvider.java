package net.jetensky.keyboard3djava.imgprocessor;

import org.opencv.core.Mat;

public interface MatProvider {
    Mat matOutput();
    Mat toProcess();
}
