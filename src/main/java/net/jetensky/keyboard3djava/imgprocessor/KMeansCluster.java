package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import net.jetensky.keyboard3djava.util.FileSystemUtil;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KMeansCluster extends AbstractImgProcessor {
    int clusterCount = 2;

    @Override
    public Img process(Img img) {
        setToProcess(img.getMat());


        List<Mat> clusters = cluster(toProcess, clusterCount);

        int clusterIndex = 0;
        for (Mat cluster : clusters) {
            String filename = "Cluster_" + clusterCount + "_from_VALUE_" + clusterIndex;

            if (clusterIndex == mostBlackCenter) {
                filename += "winner";
                Core.bitwise_not(cluster, cluster);
                FileSystemUtil.writeTraceImage(img.getFolder(), filename, cluster);

                Img from = img.from(cluster);
                from.setSingleChannel(true);
                return from;
            }
            clusterIndex++;

            cluster.release();
        }
        throw new IllegalStateException("Cluster with most black center was not found");
    }

    public List<Mat> cluster(Mat cutout, int k) {
        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());

        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);

        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

        samples.release();
        samples32f.release();

        return showClusters(cutout, labels, centers, k);
    }

    int mostBlackCenter = 0;

    private List<Mat> showClusters(Mat cutout, Mat labels, Mat centers, int k) {
        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(k);

        List<Mat> clusters = new ArrayList<Mat>();
        double mostBlackCenterValue = 255;
        for(int i = 0; i < centers.rows(); i++) {
            double currentCenterValue = centers.get(i, 0)[0];
            if (currentCenterValue <mostBlackCenterValue) {
                mostBlackCenter = i;
                mostBlackCenterValue = currentCenterValue;
            }
            clusters.add(Mat.zeros(cutout.size(), cutout.type()));
        }

        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for(int i = 0; i < centers.rows(); i++) counts.put(i, 0);

        int rows = 0;
        for(int y = 0; y < cutout.rows(); y++) {
            for(int x = 0; x < cutout.cols(); x++) {
                int label = (int)labels.get(rows, 0)[0];
                counts.put(label, counts.get(label) + 1);
                // clusters.get(label).put(y, x, b, g, r);
                int b = (int)centers.get(label, 0)[0];

                // clusters.get(label).put(y, x, label== mostBlackCenterInt ?0:255);
                clusters.get(label).put(y, x, 255);
                // System.out.println("\t" + y + "\t" + "\t" + x + "\t" + b);
                rows++;
            }
        }
        return clusters;
    }

    public void setClusterCount(int clusterCount) {
        this.clusterCount = clusterCount;
    }
}
