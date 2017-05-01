package net.jetensky.keyboard3djava.util;

import java.util.List;

public class AwtPointUtil {
    public static org.opencv.core.Point avg(List<org.opencv.core.Point> lastHandPositions, int smoothedAvgCount) {
        long sumX = 0;
        long sumY = 0;
        for (org.opencv.core.Point lastHandPosition : lastHandPositions) {
            sumX+= lastHandPosition.x;
            sumY+= lastHandPosition.y;
        }
        int newPositionX = (int) (sumX/ smoothedAvgCount);
        int newPositionY = (int) (sumY/ smoothedAvgCount);
        return new org.opencv.core.Point(newPositionX, newPositionY);
    }
}
