package net.jetensky.keyboard3djava.app;

import net.jetensky.keyboard3djava.util.AwtPointUtil;
import net.jetensky.keyboard3djava.util.ImageUtil;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class KeyboardStateEngine {
    boolean resetEnvDistances = true;
    private Mat envDistances;
    private Mat keyboardGaps;
    private Point handPointer;
    private ImageUtil.Blob handBlob;
    private EightPen2D eightPen2D;
    private Point handPointerSmoothed;
    List<Point> lastHandPositions = new ArrayList<>();


    public ActionListener resetEnvDistances() {
        return e -> {
            resetEnvDistances = true;
        };
    }

    int smoothedAvgCount = 4;


    public void processDepth(Mat depthMat) {
        if (resetEnvDistances) {
            resetEnvDistances = false;
            envDistances = depthMat.clone();
            keyboardGaps = new Mat(depthMat.size(), CvType.CV_8U);
            int gapThickness = 10;
            EightPen2DDrawer.drawCircles(eightPen2D, keyboardGaps, new Scalar(255), gapThickness);
            EightPen2DDrawer.drawCrosses(eightPen2D, keyboardGaps, new Scalar(255), gapThickness);
        }
        if (envDistances!=null) {
            Mat tableDistances = calcTableDistances(depthMat);
            Mat handBinary = calcErodedHandBinary(tableDistances);
            Mat handBinarySingle = extractSingleHandBinary(handBinary);
            List<ImageUtil.Blob> blobs = ImageUtil.detectBlobs(handBinarySingle, Imgproc.CHAIN_APPROX_SIMPLE);
            if (!blobs.isEmpty()) {
                handBlob = blobs.get(0);
                handPointer = calculateHandPointer(handBlob);

                if (handPointer!=null) {
                    if (lastHandPositions.size()>= smoothedAvgCount) {
                        lastHandPositions.remove(0);
                    }
                    lastHandPositions.add(new Point((int) handPointer.x, (int) handPointer.y));
                }

                handPointerSmoothed = AwtPointUtil.avg(lastHandPositions, smoothedAvgCount);

                boolean first = true;
                for (ImageUtil.Blob blob : blobs) {
                    if (first) {
                        first = false;
                    } else {
                        blob.contour.release();
                    }
                }
            } else {
                handBlob = null;
                handPointer = null;
            }

            tableDistances.release();
            handBinary.release();
            handBinarySingle.release();
        }
    }

    public boolean isInGap() {
        if (keyboardGaps==null) return false;
        if (handPointerSmoothed!=null) {
            double[] pixel = keyboardGaps.get((int) handPointerSmoothed.y, (int) handPointerSmoothed.x);
            return pixel[0] > 254;
        }
        return false;
    }

    private Point calculateHandPointer(ImageUtil.Blob handBlob) {
        int minY = 1000;
        int handPointerX = 0;
        for (Point point : handBlob.contour.toArray()) {
            if (point.y<minY) {
                minY = (int) point.y;
                handPointerX = (int) point.x;
            }
        }
        return new Point(handPointerX, minY);
    }

    private Mat extractSingleHandBinary(Mat handBinary) {
        List<Mat> channels = new ArrayList<>();
        Core.split(handBinary, channels);
        return channels.get(0);
    }

    private Mat calcErodedHandBinary(Mat tableDistances) {
        Mat handBinary = new Mat();
        Imgproc.threshold(tableDistances, handBinary, 15, 255, Imgproc.THRESH_BINARY)        ;

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));
        Imgproc.erode(handBinary, handBinary, kernel);
        kernel.release();
        return handBinary;
    }

    private Mat calcTableDistances(Mat depthMat) {
        Mat tableDistances = new Mat();
        Core.subtract(envDistances, depthMat, tableDistances);
        return tableDistances;
    }

    public Point getHandPointer() {
        return handPointer;
    }

    public ImageUtil.Blob getHandBlob() {
        return handBlob;
    }

    public void setEightPen2D(EightPen2D eightPen2D) {
        this.eightPen2D = eightPen2D;
    }

    public EightPen2D getEightPen2D() {
        return eightPen2D;
    }

    public Point getHandPointerSmoothed() {
        return handPointerSmoothed;
    }
}
