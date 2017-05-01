package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import net.jetensky.keyboard3djava.util.FileSystemUtil;
import net.jetensky.keyboard3djava.util.MatManager;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KMeansColorCluster extends AbstractImgProcessor {

    private int colorToBgrConversionCode = Imgproc.COLOR_Lab2BGR;
    private int clusterCount = 3;
    private int mergeLastMostBlackClusterCount = Math.min(2, clusterCount-1);
    private Mat originalImage;

    public void setClusterCount(int clusterCount) {
        if (clusterCount<=1) throw new IllegalArgumentException("Cluster count must be at least 2");
        this.clusterCount = clusterCount;
    }

    public void setMergeLastMostBlackClusterCount(int mergeLastMostBlackClusterCount) {
        this.mergeLastMostBlackClusterCount = mergeLastMostBlackClusterCount;
    }

    public void setColorToBgrConversionCode(int colorToBgrConversionCode) {
        this.colorToBgrConversionCode = colorToBgrConversionCode;
    }

    @Override
    public Img process(Img img) {
        setToProcess(img.getMat());

        List<ClusterInfo> clusters = clusters(toProcess, clusterCount);

        MatManager matManager = new MatManager();

        // We will start with all black
        Mat result = Mat.zeros(img.getMat().size(), CvType.CV_8UC1);

        try {
            int clusterIndex = 0;
            for (ClusterInfo clusterInfo : clusters) {
                Mat cluster = clusterInfo.cluster;
                String filename = "Cluster_from_" + clusterIndex + "_" + Math.round(clusterInfo.clusterAvgValue);

                Mat clusterBgr = new Mat();
                Imgproc.cvtColor(clusterInfo.cluster, clusterBgr, colorToBgrConversionCode);

                FileSystemUtil.writeTraceImage(img.getFolder(), filename, clusterBgr);

                if (clusterIndex<=(mergeLastMostBlackClusterCount-1)) {
                    Mat valueChannel = new Mat();
                    Core.extractChannel(cluster, valueChannel, 0);
                    Mat binaryCluster = new Mat();
                    Imgproc.threshold(valueChannel,binaryCluster, 1, 255, Imgproc.THRESH_BINARY)        ;

                    Core.bitwise_or(result, binaryCluster, result);
                }

                clusterIndex++;
            }
        } finally {
            matManager.releaseMats();
        }
        return img.from(result);
    }

    public List<ClusterInfo> clusters(Mat sourceImg, int k) {

        Mat samples32f = new Mat();
        Mat allPixelsInOneRow = sourceImg.reshape(1, sourceImg.cols() * sourceImg.rows());

        allPixelsInOneRow.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);
        allPixelsInOneRow.release();

        Mat labels = new Mat();
        Mat centers = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);

        /*kmeans    (Mat data, int K, Mat bestLabels, TermCriteria criteria, int attempts, int flags, Mat centers) {*/
        Core.kmeans(samples32f, k,        labels,                  criteria,            1, Core.KMEANS_PP_CENTERS, centers);

        samples32f.release();

        List<ClusterInfo> clusterInfos = createClustersBinaryImages(sourceImg, labels, centers, k);
        clusterInfos.sort((o1, o2) -> new Double(o1.clusterAvgValue).compareTo(new Double(o2.clusterAvgValue)));
        return clusterInfos;
    }

    private List<ClusterInfo> createClustersBinaryImages(Mat sourceImg, Mat labels, Mat centers, int k) {
        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(k);

        Mat quantizedImg = new Mat(sourceImg.size(), originalImage.type());

        List<ClusterInfo> clusterInfos = new ArrayList<ClusterInfo>();

        for(int i = 0; i < centers.rows(); i++) {
            double currentCenterValue = centers.get(i, 0)[0];
            Mat clusterMat = Mat.zeros(sourceImg.size(), sourceImg.type());
            ClusterInfo clusterInfo = new ClusterInfo(clusterMat, currentCenterValue);
            clusterInfos.add(clusterInfo);
        }

        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for(int i = 0; i < centers.rows(); i++) counts.put(i, 0);

        int rows = 0;
        for(int y = 0; y < sourceImg.rows(); y++) {
            for(int x = 0; x < sourceImg.cols(); x++) {
                int label = (int)labels.get(rows, 0)[0];
                counts.put(label, counts.get(label) + 1);
                // clusters.get(label).put(y, x, b, g, r);
                Mat currentCenterValue = centers.row(label);
                double[] pixelInSourceImg = sourceImg.get(y,x);

                double c1 = currentCenterValue.get(0, 0)[0];
                double c2 = currentCenterValue.get(0, 1)[0];
                double c3 = currentCenterValue.get(0, 2)[0];
                // clusters.get(label).put(y, x, label== mostBlackCenterInt ?0:255);
                // clusterInfos.get(label).cluster.put(y, x, new double[] { c1,c2,c3});
                clusterInfos.get(label).cluster.put(y, x, pixelInSourceImg);
                quantizedImg.put(y,x,new double[] { c1,c2,c3});

                // System.out.println("\t" + y + "\t" + "\t" + x + "\t" + b);
                rows++;
            }
        }
        Imgproc.cvtColor(quantizedImg, quantizedImg, colorToBgrConversionCode);
        FileSystemUtil.writeTraceImage("/tmp/","q",quantizedImg);
        quantizedImg.release();
        return clusterInfos;
    }

    public void setOriginalImage(Mat originalImage) {
        this.originalImage = originalImage;
    }


    public class ClusterInfo {
        /**
         * If pixel is assigned to this cluster, its value is white, otherwise its value is black
         */
        Mat cluster;
        double clusterAvgValue;

        public ClusterInfo(Mat cluster, double clusterAvgValue) {
            this.cluster = cluster;
            this.clusterAvgValue = clusterAvgValue;
        }

    }


}
