
package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import net.jetensky.keyboard3djava.util.DrawUtil;
import net.jetensky.keyboard3djava.util.ImageUtil;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlobDetection extends AbstractImgProcessor {

    private MatProvider matToCutBlobsFrom;
    private MatProvider matForDebug;
    private List<FoundBlob> foundBlobs = new ArrayList<>();
	private Double blobWidthPagePercentThreshold;
	private Double blobHeightPagePercentThreshold;
    private int approxMode = Imgproc.CHAIN_APPROX_SIMPLE;

    public BlobDetection(MatProvider matToCutBlobsFrom, MatProvider matForDebug) {
        this.matToCutBlobsFrom = matToCutBlobsFrom;
        this.matForDebug = matForDebug;
    }

	@Override
	public Img process(Img img) {
	    if (!foundBlobs.isEmpty()) throw new IllegalStateException("BlobDetection cannot be reused, please create one instance for each pipeline processing");
        setToProcess(img.getMat());

        List<ImageUtil.Blob> blobs = ImageUtil.detectBlobs(toProcess, approxMode);

        Mat imgToCutBlobsFrom = matToCutBlobsFrom.matOutput();

        Mat debugImg = matForDebug.matOutput().clone();
        int blobIndex = 0;

        double widthThreshold = blobWidthPagePercentThreshold==null? -1 : blobWidthPagePercentThreshold * toProcess.width();
        double heightThreshold = blobHeightPagePercentThreshold==null? -1: blobHeightPagePercentThreshold * toProcess.height();

        for (ImageUtil.Blob blob : blobs) {
            String foundBlobIndex;

            MatOfPoint contour = blob.contour;
            Rect boundingRect = Imgproc.boundingRect(contour);


			if (boundingRect.width < widthThreshold) {
                    log.debug("Skipping blob #" + blobIndex + " as it is too small. Width " + boundingRect.width + " < " + widthThreshold);
			} else if (boundingRect.height < heightThreshold) {
                    log.debug("Skipping blob #" + blobIndex + " as it is too small. Height " + boundingRect.height + " < " + heightThreshold);
			} else {
                log.debug("Including blob #" + blobIndex + " big enough: width,height = " + boundingRect.width + ", " + boundingRect.height );
                DrawUtil.draw(debugImg, boundingRect, Color.GREEN);
                Mat blobMat = DrawUtil.copyRegion(imgToCutBlobsFrom, boundingRect);
                foundBlobIndex = String.valueOf(foundBlobs.size());
                foundBlobs.add(new FoundBlob(blobMat, boundingRect, contour.toArray(), blobIndex));
                if (log.isDebugEnabled()) {
                    DrawUtil.draw(debugImg, boundingRect, Color.BLACK);
                    Imgproc.putText(debugImg, "#" + blobIndex + "(" + foundBlobIndex+")", new Point(boundingRect.x + boundingRect.width/2,boundingRect.y + boundingRect.height/2),
                            Core.FONT_HERSHEY_TRIPLEX, 3.0 ,new Scalar(0,0,0));
                }
            }
            blobIndex++;
		}
		DrawUtil.drawBlobs(debugImg, blobs);
        ImageUtil.releaseBlobs(blobs);
        log.debug("Found " + foundBlobs.size() + " blobs with proper size, out of " + blobs.size());
		return new Img(img.getFolder(), debugImg);

	}

	public List<FoundBlob> getFoundBlobs() {
		return foundBlobs;
	}

    public void setBlobWidthPagePercentThreshold(Double blobWidthPagePercentThreshold) {
        this.blobWidthPagePercentThreshold = blobWidthPagePercentThreshold;
    }

    public void setBlobHeightPagePercentThreshold(Double blobHeightPagePercentThreshold) {
        this.blobHeightPagePercentThreshold = blobHeightPagePercentThreshold;
    }

    public static class FoundBlob {
        private int index;
        public Mat matWithBlob;
        public Rect boundingRect;
        public Point[] contour;

        public FoundBlob() {

        }
        public FoundBlob(Mat matWithBlob, Rect boundingRect, Point[] contour, int index) {
            this.matWithBlob = matWithBlob;
            this.boundingRect = boundingRect;
            this.contour = contour;
            MatOfPoint matOfPoint = new MatOfPoint(contour);
            matOfPoint.release();
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public Rect getBoundingRect() {
            return boundingRect;
        }
    }

    /**
     * Set some value from Imgproc.CHAIN_APPROX_???
     * Default is Imgproc.CHAIN_APPROX_SIMPLE
     */
    public void setApproxMode(int approxMode) {
        this.approxMode = approxMode;
    }

}
