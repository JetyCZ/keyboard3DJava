package net.jetensky.keyboard3djava.util.swing;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.openkinect.freenect.FrameMode;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

public class SwingUtils {

    static float multiplicator = (float) 256/500;

    public static Mat bufferDepthToMat(FrameMode mode, ByteBuffer depthFrameInput) {
        Mat result = new Mat(mode.getHeight(), mode.getWidth(), CvType.CV_8UC3);
        int indexIn = 0;

        for(int y = 0; y < mode.getHeight(); ++y) {
            int indexOut = y * mode.width;

            for(int x = 0; x < mode.width; ++indexOut) {

                int depthInt  = depthFrameInput.get(indexIn++) & 255 | (depthFrameInput.get(indexIn++) & 255) << 8;

                int value;
                if (depthInt == 0) {
                    value = 0;
                } else {
                    value = (int) ((depthInt - 500) * multiplicator);
                }
                result.put(480-y-1, 640-x-1, value, value, value);
                // if (y>240)
                // result.put(y, x, 255,0,255);
                ++x;
            }
        }
        return result;
    }

    public static BufferedImage matToBufferedImage(Mat frame) {
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        return writeMatToBufferedImage(frame, image);
    }

    public static BufferedImage writeMatToBufferedImage(Mat frame, BufferedImage image) {
        WritableRaster raster = image.getRaster();
        DataBufferInt dataBuffer = (DataBufferInt) raster.getDataBuffer();
        int[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }


    /**
     * Converts/writes a Mat into a BufferedImage.
     *
     * @param matrix Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
     */
    public static BufferedImage matToBufferedImage2(Mat matrix, BufferedImage bimg)
    {
        if ( matrix != null ) {
            int cols = matrix.cols();
            int rows = matrix.rows();
            int elemSize = (int)matrix.elemSize();
            byte[] data = new byte[cols * rows * elemSize];
            int type;
            matrix.get(0, 0, data);
            switch (matrix.channels()) {
                case 1:
                    type = BufferedImage.TYPE_BYTE_GRAY;
                    break;
                case 3:
                    type = BufferedImage.TYPE_3BYTE_BGR;
                    // bgr to rgb
                    byte b;
                    for(int i=0; i<data.length; i=i+3) {
                        b = data[i];
                        data[i] = data[i+2];
                        data[i+2] = b;
                    }
                    break;
                default:
                    return null;
            }

            // Reuse existing BufferedImage if possible
            if (bimg == null || bimg.getWidth() != cols || bimg.getHeight() != rows || bimg.getType() != type) {
                bimg = new BufferedImage(cols, rows, type);
            }
            bimg.getRaster().setDataElements(0, 0, cols, rows, data);
        } else { // mat was null
            bimg = null;
        }
        return bimg;
    }

}
