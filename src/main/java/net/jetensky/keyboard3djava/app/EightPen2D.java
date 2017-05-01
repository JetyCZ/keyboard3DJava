package net.jetensky.keyboard3djava.app;

import net.jetensky.keyboard3djava.util.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Point;

import java.util.HashMap;
import java.util.Map;

public class EightPen2D {
    int outerRadius = 120;
    private int innerRadius = 30;
    private int bottomDistance;
    private int leftDistance;
    private StringBuffer sequence = new StringBuffer();
    private boolean collectionInProgress = false;
    private int lastSegment = -1;
    private String text = "";

    Map<String, String> sequenceToKeyMapping = new HashMap<>();

    {
        sequenceToKeyMapping.put( "32", "a");
        sequenceToKeyMapping.put(  "4321", "b");
        sequenceToKeyMapping.put( "1234", "c");
        sequenceToKeyMapping.put( "234", "d");
        sequenceToKeyMapping.put( "41", "e");
        sequenceToKeyMapping.put( "3214", "f");
        sequenceToKeyMapping.put( "2341", "g");
        sequenceToKeyMapping.put( "432", "h");
        sequenceToKeyMapping.put( "23", "i");
        sequenceToKeyMapping.put( "14321", "j");
        sequenceToKeyMapping.put( "2143", "k");
        sequenceToKeyMapping.put( "412", "l");
        sequenceToKeyMapping.put( "1432", "m");
        sequenceToKeyMapping.put( "143", "n");
        sequenceToKeyMapping.put( "34", "o");
        sequenceToKeyMapping.put( "4123", "p");
        sequenceToKeyMapping.put( "41234", "q");
        sequenceToKeyMapping.put( "321", "r");
        sequenceToKeyMapping.put( "123", "s");
        sequenceToKeyMapping.put( "43", "t");
        sequenceToKeyMapping.put( "341", "u");
        sequenceToKeyMapping.put( "12341", "v");
        sequenceToKeyMapping.put( "3412", "w");
        sequenceToKeyMapping.put( "214", "x");
        sequenceToKeyMapping.put( "21", "y");
        sequenceToKeyMapping.put( "23414", "z");
        sequenceToKeyMapping.put( "32141", "?");
        sequenceToKeyMapping.put( "21432", "'");
        sequenceToKeyMapping.put( "12", ".");
        sequenceToKeyMapping.put( "43214", "@" );
        sequenceToKeyMapping.put( "34123", "!" );
        sequenceToKeyMapping.put( "14", "," );

        sequenceToKeyMapping.put( "3", " " );

    }

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

    public String getActiveSegment(Point handPointer, boolean isInGap) {

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


        String sequenceStr = sequence.toString();
        if (lastSegment!=segment) {
            if (!isInGap) {
                if (segment==0) {
                    if (collectionInProgress) {

                        if ("1".equals(sequenceStr)) {
                            if (!StringUtils.isEmpty(text))
                                text = text.substring(0, text.length()-1);
                        } else {
                            String key = sequenceToKeyMapping.get(sequenceStr);
                            if (key!=null) {
                                text+=key;
                            }
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
        }


        return text + ":" + segment + "(" + sequenceStr + ") " + ((isInGap)?"GAP":"");
    }


}
