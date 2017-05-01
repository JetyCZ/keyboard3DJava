package net.jetensky.keyboard3djava.app;

import net.jetensky.keyboard3djava.util.MathUtil;
import org.opencv.core.Point;

public class EightPen2D {
    int outerRadius = 120;
    private int innerRadius = 30;
    private int bottomDistance;
    private int leftDistance;
    private StringBuffer sequence = new StringBuffer();
    private boolean collectionInProgress = false;
    private int lastSegment = -1;
    private String text = "";


    public void setBottomDistance(int bottomDistance) {
        this.bottomDistance = bottomDistance;
    }

    public int getOuterRadius() {
        return outerRadius;
    }

    public Point getMiddle() {
        return new org.opencv.core.Point(leftDistance, 480- bottomDistance - outerRadius);
    }

    public void setLeftDistance(int leftDistance) {
        this.leftDistance = leftDistance;
    }

    public int getLeftDistance() {
        return leftDistance;
    }

    public int getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(int innerRadius) {
        this.innerRadius = innerRadius;
    }

    public String getActiveSegment(Point handPointer) {

        if (handPointer==null) return "NO HAND";
        Point middle = getMiddle();
        double distFromMiddle = MathUtil.getLength(middle, handPointer);
        int segment = -1;
        if (distFromMiddle <innerRadius) {
            segment=0;
        } else if (distFromMiddle<outerRadius){
            double angleInDegrees = MathUtil.getAngleInDegrees(handPointer, middle);
            if (Math.abs(angleInDegrees)<=45) {
                segment=1;
            } else if (Math.abs(angleInDegrees)>=135) {
                segment=3;
            } else if (angleInDegrees<135 && angleInDegrees>45) {
                segment=2;
            } else {
                segment=4;
            }
        } else {
            segment = -1;
            sequence.setLength(0);
        }


        if (lastSegment!=segment) {
            if (segment==0) {
                if (collectionInProgress) {
                    if ("32".equals(sequence.toString())) {
                        text+="a";
                    }
                    sequence.setLength(0);
                }
                collectionInProgress = true;
            } else {
                if (collectionInProgress && segment>0) {
                    sequence.append(segment);
                }
            }

            lastSegment = segment;
        }


        return text + ":" + segment + "(" + sequence.toString() + ") ";
    }
}
