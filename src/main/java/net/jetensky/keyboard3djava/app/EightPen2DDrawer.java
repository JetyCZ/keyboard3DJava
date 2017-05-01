package net.jetensky.keyboard3djava.app;

import net.jetensky.keyboard3djava.util.DrawUtil;
import net.jetensky.keyboard3djava.util.MathUtil;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class EightPen2DDrawer {
    public static void drawCircles(EightPen2D eightPen2D, Mat depthMat, Scalar color, int thickness) {
        Point kMiddle = eightPen2D.getMiddle();
        Imgproc.circle(depthMat, kMiddle, eightPen2D.getInnerRadius(), color, thickness);
        Imgproc.circle(depthMat, kMiddle, eightPen2D.getOuterRadius(), color, thickness);
    }

    public static void drawCrosses(EightPen2D eightPen2D, Mat depthMat, Scalar scalar, int thickness) {
        Point kMiddle = eightPen2D.getMiddle();
        double innerRadius = eightPen2D.getInnerRadius();
        Point p1 = new Point(
                (int) kMiddle.x - innerRadius/1.41,
                (int) kMiddle.y - innerRadius/1.41
        );
        double outerRadius = eightPen2D.getOuterRadius();
        Point p2 = new Point(
                (int) kMiddle.x - outerRadius/1.41,
                (int) kMiddle.y - outerRadius/1.41
        );
        for (int i=0;i<4;i++) {
            Point rotated1 = MathUtil.rotatePointAroundAnother(p1, kMiddle, i*90);
            Point rotated2 = MathUtil.rotatePointAroundAnother(p2, kMiddle, i*90);


            DrawUtil.drawLine(depthMat, rotated1, rotated2, scalar, thickness);

        }
    }
}
