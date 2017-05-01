package net.jetensky.keyboard3djava.app;

import boofcv.struct.image.GrayU16;
import net.jetensky.keyboard3djava.util.AwtPointUtil;
import net.jetensky.keyboard3djava.util.DrawUtil;
import net.jetensky.keyboard3djava.util.ImageUtil;
import net.jetensky.keyboard3djava.util.opencv.OpencvLoaderHelper;
import net.jetensky.keyboard3djava.util.swing.ImageCanvas;
import net.jetensky.keyboard3djava.util.swing.SwingUtils;
import net.jetensky.keyboard3djava.util.swing.UI;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openkinect.freenect.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static net.jetensky.keyboard3djava.util.swing.SwingUtils.bufferDepthToMat;

/**
 * Example demonstrating how to process and display data from the Kinect.
 *
 * @author Peter Abeles
 */
public class Keyboard3D {

    EightPen2D eightPen2D = new EightPen2D();
	ImageCanvas imageCanvas;
	GrayU16 depthGrayU16 = new GrayU16(1,1);
	BufferedImage outDepthImage;
	KeyboardStateEngine keyboardStateEngine = new KeyboardStateEngine();
    UI ui = new UI();

    public static void main( String args[] ) {
        OpencvLoaderHelper.initOpenCV();
        Keyboard3D app = new Keyboard3D();
        app.process();
    }

	public void process() {
		Context kinect = Freenect.createContext();
 
		if( kinect.numDevices() < 0 )
			throw new RuntimeException("No kinect found!");
 
		Device device = kinect.openDevice(0);
 
		device.setDepthFormat(DepthFormat.REGISTERED);
		device.setVideoFormat(VideoFormat.RGB);
 
		device.startDepth((mode, frame, timestamp) -> processDepth(mode,frame));
		/*device.startVideo(new VideoHandler() {
			@Override
			public void onFrameReceived(FrameMode mode, ByteBuffer panel1, int timestamp) {
				processRgb(mode,panel1,timestamp);
			}
		});*/
 
		ui.waitForSpace();

		device.stopDepth();
		device.stopVideo();
		device.close();

        ui.close();
 
	}

	List<Point> lastHandPositions = new ArrayList<>();

 
	protected void processDepth(FrameMode mode, ByteBuffer depthFrame) {

		if( outDepthImage == null ) {
			depthGrayU16.reshape(mode.getWidth(),mode.getHeight());
			outDepthImage = new BufferedImage(depthGrayU16.width, depthGrayU16.height,BufferedImage.TYPE_3BYTE_BGR);
			ui.showWindow(outDepthImage,"Depth Image", keyboardStateEngine.resetEnvDistances());
            imageCanvas = new ImageCanvas(outDepthImage);
		}
		eightPen2D.setBottomDistance(UI.bottomValue());
		eightPen2D.setLeftDistance(UI.leftValue());

        Mat depthMat;
		depthMat = bufferDepthToMat(mode, depthFrame);

		keyboardStateEngine.processDepth(depthMat);

		drawInfoGraphics(depthMat, eightPen2D);
		ui.debug(eightPen2D.getActiveSegment(keyboardStateEngine.getHandPointer()));

		// FileSystemUtil.saveImage(depthMat, "/tmp/debug/a.png");
		SwingUtils.matToBufferedImage2(depthMat, outDepthImage);
		depthMat.release();

        bufferDepthToU16Mirror(depthFrame, depthGrayU16);

		ui.frame.repaint();
	}

	private void drawInfoGraphics(Mat depthMat, EightPen2D eightPen2D) {
		ImageUtil.Blob handBlob = keyboardStateEngine.getHandBlob();
		int smoothedAvgCount = 4;
		if (handBlob!=null) {
			DrawUtil.drawContour(depthMat, handBlob.contour, Color.CYAN);
			Point handPointer = keyboardStateEngine.getHandPointer();
			if (handPointer!=null) {
				if (lastHandPositions.size()>= smoothedAvgCount) {
					lastHandPositions.remove(0);
				}
				lastHandPositions.add(new Point((int) handPointer.x, (int) handPointer.y));
			}

			Point hand = AwtPointUtil.avg(lastHandPositions, smoothedAvgCount);
			int handX = (int) hand.x;
			int handY = (int) hand.y;
			DrawUtil.drawLine(depthMat, new Point(handX-10, handY-10), new Point(handX+10, handY+10), new Scalar(128,64,255));
			DrawUtil.drawLine(depthMat, new Point(handX+10, handY-10), new Point(handX-10, handY+10), new Scalar(128,64,255));
			handBlob.contour.release();
		}
		Point kMiddle = eightPen2D.getMiddle();
		Imgproc.circle(depthMat, kMiddle, eightPen2D.getInnerRadius(), new Scalar(64,255,128));
		Imgproc.circle(depthMat, kMiddle, eightPen2D.getOuterRadius(), new Scalar(64,255,128));

		Point p1 = new Point(
				(int) kMiddle.x - eightPen2D.getInnerRadius()*1.41,
				(int) kMiddle.y - eightPen2D.getInnerRadius()*1.41
				);
		for (int i=0;i<4;i++) {
			int crossLength = (int) Math.sqrt(eightPen2D.getOuterRadius()*eightPen2D.getOuterRadius()*0.5 - eightPen2D.getInnerRadius());
			DrawUtil.drawLine(depthMat, new Point(kMiddle.x- crossLength, kMiddle.y- crossLength), new Point(kMiddle.x+ crossLength, kMiddle.y+ crossLength), new Scalar(128,64,255));
			DrawUtil.drawLine(depthMat, new Point(kMiddle.x- crossLength, kMiddle.y+ crossLength), new Point(kMiddle.x+ crossLength, kMiddle.y- crossLength), new Scalar(128,64,255));
		}
	}

	private void bufferDepthToU16Mirror(ByteBuffer depthFrameInput, GrayU16 matOutput) {
            int indexIn = 0;

            for(int y = 0; y < matOutput.height; ++y) {
                int indexOut = matOutput.startIndex + (matOutput.getHeight() - 1 - y) * matOutput.stride;

                int myIndex = indexOut + matOutput.width -1;
                for(int x = 0; x < matOutput.width; ++indexOut,myIndex--) {
                    matOutput.data[myIndex] = (short)(depthFrameInput.get(indexIn++) & 255 | (depthFrameInput.get(indexIn++) & 255) << 8);
                    ++x;
                }
            }
    }


	/*protected void processRgb( FrameMode mode, ByteBuffer frame, int timestamp ) {
		if( mode.getVideoFormat() != VideoFormat.RGB ) {
			System.out.println("Bad rgb format!");
		}
 
		System.out.println("Got rgb!   "+timestamp);
 
		if( outRgb == null ) {
			rgb.reshape(mode.getWidth(),mode.getHeight());
			outRgb = new BufferedImage(rgb.width,rgb.height,BufferedImage.TYPE_INT_RGB);
			guiRgbPanel = ui.showWindow(outRgb,"RGB Image");
		}
 
		UtilOpenKinect.bufferRgbToMsU8(frame, rgb);
		ConvertBufferedImage.convertTo_U8(rgb,outRgb,true);
 
		guiRgbPanel.repaint();
	}*/
 
}