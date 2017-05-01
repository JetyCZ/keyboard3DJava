package net.jetensky.keyboard3djava.imgprocessor;


import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Mat;

public interface ImgProcessor extends MatProvider{

    public Img process(Img img);
    public void setMatOutput(Mat matOutput);

}
