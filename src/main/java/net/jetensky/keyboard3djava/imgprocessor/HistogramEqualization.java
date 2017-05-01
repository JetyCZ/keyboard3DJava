
package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class HistogramEqualization extends AbstractImgProcessor {


	@Override
	public Img process(Img img) {

		Mat equ = new Mat();
		/*Imgproc.cvtColor(img.getMat(), equ, Imgproc.COLOR_BGR2YCrCb);*/
		List<Mat> channels = new ArrayList<Mat>();
		Core.split(img.getMat(), channels);
		Imgproc.equalizeHist(channels.get(0), channels.get(0));
		Core.merge(channels, equ);
		/*Imgproc.cvtColor(equ, equ, Imgproc.COLOR_YCrCb2BGR);*/
		return img.from(equ);

	}


}
