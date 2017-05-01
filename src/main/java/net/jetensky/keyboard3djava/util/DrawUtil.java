package net.jetensky.keyboard3djava.util;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class DrawUtil {
    private final static Logger log = LoggerFactory.getLogger(DrawUtil.class);

    public static void drawContour(Mat img, MatOfPoint contour, Color color) {
        Imgproc.drawContours(img, Collections.singletonList(contour), 0, new Scalar(color.getBlue(), color.getGreen(), color.getRed()),2);
    }

    public static void drawContours(Mat img, List<MatOfPoint> contours, Color color) {
        for (MatOfPoint contour : contours) {
            drawContour(img, contour, color);
        }
    }

    public static void drawBlobs(Mat img, List<ImageUtil.Blob> blobs) {
        for (ImageUtil.Blob blob : blobs) {
            Color color = Color.CYAN;
            if (blob.containsHole) {
                color = Color.RED;
            } else {
                color = Color.YELLOW;
            }
            DrawUtil.drawContour(img, blob.contour, color);
        }
    }

    public static void draw(Mat img, Rect rect, Color color) {
        Scalar colorScalar = new Scalar(color.getBlue(), color.getGreen(), color.getRed());
        draw(img, rect, colorScalar);
    }

    public static void draw(Mat img, Rect rect, Scalar color) {
        if (rect==null) {
            log.warn("Rect is null");
            return;
        }
        Point topLeft = new Point(rect.x, rect.y);
        int right = rect.x + rect.width;
        Point topRight = new Point(right, rect.y);
        int bottom = rect.y + rect.height;
        Point bottomLeft = new Point(rect.x, bottom);
        Point bottomRight = new Point(right, bottom);

        drawLine(img, topLeft, topRight, color);
        drawLine(img, topRight, bottomRight, color);
        drawLine(img, bottomRight, bottomLeft, color);
        drawLine(img, bottomLeft, topLeft, color);
    }

    public static void drawLine(Mat bill, Point from, Point to, Scalar color) {
        Imgproc.line(bill, from, to, color,1);
    }

    public static Mat copyRegion(Mat source, Rect region) {
        return new Mat(source, region).clone();
    }

    public static void fillRect(Mat mat, Rect rect, Scalar rectColor) {
        MatOfPoint points = new MatOfPoint(
                new Point(rect.x, rect.y),
                new Point(rect.x + rect.width-1, rect.y),
                new Point(rect.x + rect.width-1, rect.y + rect.height-1),
                new Point(rect.x, rect.y + rect.height-1)
        );
        Imgproc.fillConvexPoly(mat, points, rectColor);
    }

    public static Scalar toScalar(Color color) {
        return new Scalar(color.getBlue(), color.getGreen(), color.getRed());
    }
}
