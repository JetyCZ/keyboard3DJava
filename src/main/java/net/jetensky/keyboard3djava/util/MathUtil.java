package net.jetensky.keyboard3djava.util;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

public class MathUtil {
    public static double getLength(Point p1, Point p2) {
        return Math.sqrt(((Math.abs(p1.x) - Math.abs(p2.x)) * (Math.abs(p1.x) - Math.abs(p2.x))) + ((Math.abs(p1.y) - Math.abs(p2.y)) * (Math.abs(p1.y) - Math.abs(p2.y))));
    }

    /**
        Returns angle between 0 and 360
     */
    public static double getAngleInDegrees(Point p1, Point p2) {
            float angle = (float) Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x));
            /*if(angle < 0){
                angle += 360;
            }*/
            return angle;
        }

    public static List<Point> mergeTwoClosestPoints(List<Point> simplifiedContour) {
        if (simplifiedContour.size()!=5) throw new IllegalArgumentException("This method can merge only 5 points ressembling square into 4 points");

        double minDistance = Double.MAX_VALUE;
        int mostClosePairIndex = -1;
        for (int pointIndex = 0;pointIndex<simplifiedContour.size();pointIndex++) {

            Point p1= simplifiedContour.get(pointIndex);

            int nextPointIndex = nextPointIndexInContour(simplifiedContour, pointIndex);
            Point p2= simplifiedContour.get(nextPointIndex);

            double distance = MathUtil.getLength(p1, p2);
            if (distance<minDistance) {
                minDistance = distance;
                mostClosePairIndex = pointIndex;
            }
        }

        List<Point> result = new ArrayList<>();
        for (int pointIndex = 0;pointIndex<simplifiedContour.size();pointIndex++) {

            if (pointIndex==mostClosePairIndex) {
                int nextPointIndex = nextPointIndexInContour(simplifiedContour, pointIndex);
                Point mergePoint1 = simplifiedContour.get(pointIndex);
                Point mergePoint2 = simplifiedContour.get(nextPointIndex);
                Point mergedPoint = new Point((mergePoint1.x + mergePoint2.x)/2, (mergePoint1.y + mergePoint2.y)/2);
                result.add(mergedPoint);
                pointIndex++;
            } else {
                if (!((pointIndex == 0) && (mostClosePairIndex==simplifiedContour.size()-1))) {
                    result.add(simplifiedContour.get(pointIndex));
                }
            }
        }
        return result;
    }

    private static int nextPointIndexInContour(List<Point> simplifiedContour, int pointIndex) {
        int compareToIndex = pointIndex + 1;
        if (compareToIndex==simplifiedContour.size()) compareToIndex=0;
        return compareToIndex;
    }

    public static Point middle(Rect rect) {
        return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
    }

    public static boolean overlaps(Rect rect1, Rect rect2) {
        return
                inBetween(rect1.x + rect1.width-1, rect2.x, rect2.x+rect2.width-1) ||
                inBetween(rect2.x + rect2.width-1, rect1.x, rect1.x+rect1.width-1)
                ;
    }

    private static boolean inBetween(double value, double lowValue, double highValue) {
        return (value>=lowValue) && (value<=highValue);
    }

    public static boolean differsMoreThanPercent(double value1, double value2, int percentThreshold) {
        double thresholdRange = (double) percentThreshold/100;
        return !inBetween((double) value1/value2, 1 - thresholdRange, 1 + thresholdRange);
    }

    public static Point rotatePointAroundAnother(Point toRotate, Point rotationMiddle, int angleDegrees) {
        double angle = (double) 2*Math.PI* ((double) angleDegrees/360);
        Point rotated = new Point();
        double x1 = toRotate.x;
        double y1 = toRotate.y;
        double x0 = rotationMiddle.x;
        double y0 = rotationMiddle.y;
        rotated.x = ((x1 - x0) * Math.cos(angle)) - ((y1 - y0) * Math.sin(angle)) + x0;
        rotated.y = ((x1 - x0) * Math.sin(angle)) + ((y1 - y0) * Math.cos(angle)) + y0;
        return rotated;

    }
}
