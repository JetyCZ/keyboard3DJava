package net.jetensky.keyboard3djava.util;

import net.jetensky.keyboard3djava.imgprocessor.BlobDetection;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImageUtil {

    public static MatOfPoint approxPolygonFromCurve(MatOfPoint matOfPoint) {
        return approxPolygonFromCurve(matOfPoint, 0.02);
    }

    public static MatOfPoint approxPolygonFromCurve(MatOfPoint matOfPoint, double relativeDistance) {
        MatOfPoint2f contour2f = null;
        MatOfPoint2f approxCurve = null;
        try {
            contour2f = new MatOfPoint2f(matOfPoint.toArray());
            double approxDistance = Imgproc.arcLength(contour2f, true)*relativeDistance;
            approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());
            return points;
        } finally {
            if (contour2f!=null) contour2f.release();
            if (approxCurve!=null) contour2f.release();
        }
    }

    public static Rect expandRect(Rect rect, double relativeWidthChange, double relativeHeightChange, Mat boundaryLimit) {
        Rect result = new Rect();
        double deltaX = ((relativeWidthChange - 1) * rect.width)/2;
        double deltaY = ((relativeHeightChange - 1) * rect.width)/2;

        result.x = (int) Math.round(rect.x - deltaX);
        result.x = Math.max(result.x, 0);

        result.y = (int) Math.round(rect.y - deltaY);
        result.y = Math.max(result.y, 0);


        result.width = (int) (rect.width * relativeWidthChange);
        result.width = Math.min(result.width, boundaryLimit.width() - result.x);

        result.height = (int) (rect.height* relativeHeightChange);
        result.height = Math.min(result.height, boundaryLimit.height() - result.y);
        return result;
    }

    public static void releaseMats(Mat... mats) {
        for (Mat mat : mats) {
            if (mat!=null) mat.release();
        }
    }

    public static Mat erode(Mat source, int erosionKernelSize) {
        return erode(source, erosionKernelSize, Imgproc.MORPH_RECT);
    }
    public static Mat erode(Mat source, int erosionKernelSize, int structuringElemShape) {
        Mat dest = new Mat();
        Imgproc.erode(source, dest, Imgproc.getStructuringElement(structuringElemShape, new Size(erosionKernelSize, erosionKernelSize)));
        return dest;
    }

    public static void invert(Mat mat) {
        Mat invertcolormatrix = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(255,0,255));
        Core.subtract(invertcolormatrix, mat, mat);
        invertcolormatrix.release();
    }

    public static void releaseBlobs(List<Blob> blobs) {
        for (Blob blob : blobs) {
            if (blob.contour!=null) blob.contour.release();
        }
    }

    public static void releaseFoundBlobs(List<BlobDetection.FoundBlob> foundBlobs) {
        if (foundBlobs!=null)
            for (BlobDetection.FoundBlob foundBlob : foundBlobs) {
                if (foundBlob.matWithBlob!=null) foundBlob.matWithBlob.release();
            }
    }

    public static void releaseMatOfPoints(List<MatOfPoint> contours) {
        for (MatOfPoint matOfPoint : contours) {
            matOfPoint.release();
        }
    }

    public static class Blob {
        public MatOfPoint contour;
        public boolean containsHole;
    }

    public static List<Blob> detectBlobs(Mat blackAndWhite, int approxMode) {
        List<Blob> blobs = new ArrayList<>();
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(blackAndWhite, contours, hierarchy, Imgproc.RETR_TREE, approxMode);

        int contourIndex =0;
        for (MatOfPoint contour : contours) {
            // 0 next contour (same level), 1 previous contour (same level), 2 child contour, 3 parent contour

            double[] contourHierarchy = hierarchy.get(0, contourIndex);
            double childContour = contourHierarchy[2];
            // double parentContour = contourHierarchy[3];
            Blob blob = new Blob();
            blob.contour = contour;
            blob.containsHole = (childContour>-1);
            blobs.add(blob);
            contourIndex ++;
        }
        return  blobs;
    }

    public static Mat rotateByAngle(double angleInDegrees, Mat src) {
        Mat dst = new Mat();
        int size = src.cols() > src.rows() ? src.cols() : src.rows();
        Point pt = new Point(size/2, size/2);
        Mat r = Imgproc.getRotationMatrix2D(pt, angleInDegrees, 1.0);
        Imgproc.warpAffine(src, dst, r, new Size(size, size));
        r.release();
        return dst;
    }

    public static Mat dilate(Mat source, int dilationKernelHeight, int dilationKernelWidth) {
        Mat dilated = new Mat();
        Imgproc.dilate(source, dilated, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(dilationKernelWidth,dilationKernelHeight)));
        return dilated;
    }

    public static Rect rectFromBBoxText(Size size, String[] elems) {
        Rect rect = new Rect();
        rect.x = Integer.parseInt(elems[1]);
        rect.width = Integer.parseInt(elems[3]) - rect.x;
        int fromYZeroIsBottom = Integer.parseInt(elems[2]);
        int toYZeroIsBottom = Integer.parseInt(elems[4]);
        rect.height = toYZeroIsBottom - fromYZeroIsBottom;

        rect.y = (int) (size.height - fromYZeroIsBottom - rect.height);
        return rect;
    }

}
