
package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import net.jetensky.keyboard3djava.util.ImageUtil;
import org.opencv.core.Mat;

public class Inversion extends AbstractImgProcessor {

	@Override
	public Img process(Img img) {
		setToProcess(img.getMat());

		Mat inverted = toProcess.clone();
		ImageUtil.invert(inverted);

		return img.from(inverted);
	}


}
