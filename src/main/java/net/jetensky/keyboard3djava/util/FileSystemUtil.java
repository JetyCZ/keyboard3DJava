package net.jetensky.keyboard3djava.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class FileSystemUtil {
    public static Logger log = LoggerFactory.getLogger(FileSystemUtil.class);

    public static BufferedImage toBufferedImage(Mat mat) throws IOException {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        return ImageIO.read(new ByteArrayInputStream(matOfByte.toArray()));
    }

    public static void saveImage(Mat mat, String out) {
        log.debug("saving image to "+out);
        String ext = getExtension(out);
        if("png".equalsIgnoreCase(ext)) {
            Imgcodecs.imwrite(out, mat, new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION, 0));
        } else { //jpg
            Imgcodecs.imwrite(out, mat, new MatOfInt(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, 100));
        }
        if (log.isTraceEnabled())
            addToFileSizeLog(out);
    }

    private static void addToFileSizeLog(String out) {
        String stats = out + "\t" + new File(out).length() + "\n";
        try {
            FileUtils.writeStringToFile(new File("/tmp/size.txt"), stats, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getExtension(String filename) {
        String[] tmp = filename.split("\\.");
        return tmp[tmp.length-1];
    }

    public static String writeTraceImage(Mat img) {
        return writeTraceImage("/tmp/", "a",img);
    }
    public static String writeTraceImage(String folder, String filename, Mat img) {
        if (log.isTraceEnabled()) {
            String addedSlash = folder.endsWith("/")?"":"/";
            return writeTemporaryImage(folder + addedSlash + "debug/", filename, img);
        }
        return null;
    }

    public static String writeTemporaryImage(String folder, String filename, Mat img) {
        String fullFileName = createFolderForFile(folder, filename, ".png");
        saveImage(img, fullFileName);
        return fullFileName;
    }

    private static String createFolderForFile(String folder, String filename, String extension) {
        String fullFileName = folder + filename + extension;
        File file = new File(fullFileName);
        try {
            FileUtils.forceMkdir(file.getParentFile());
            log.debug("Created folder " + file.getParentFile());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create folder " + file.getAbsolutePath(), e);
        }
        return fullFileName;
    }

    public static String writeDebugText(String folder, String filename, String text) {
        String fullFileName = createFolderForFile(folder, filename, ".txt");
        try {
            FileUtils.write(new File(fullFileName), text);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write text file " + fullFileName, e);
        }
        return fullFileName;
    }


    /**
     * Returns folderName of given file with ending slash
     */
    public static String folder(String filename) {
        return new File(filename).getParentFile().getAbsolutePath() + "/";
    }

    public static Mat readImage(String absolutePath) {
        checkImagePath(absolutePath);
        return Imgcodecs.imread(absolutePath);
    }

    public static void checkImagePath(String absolutePath) {
        if (StringUtils.isEmpty(absolutePath)) {
            throw new IllegalArgumentException("Absolute path cannot be empty");
        }
        if (!new File(absolutePath).exists()) {
            throw new IllegalArgumentException("File with image " + absolutePath + " does not exist");
        }
    }

    public static Mat readImage(String absolutePath, int cvLoadMode) {
        checkImagePath(absolutePath);
        return Imgcodecs.imread(absolutePath, cvLoadMode);
    }

    public static Mat readImageGrayscale(String file) {
        return readImage(file, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
    }

    public static void deleteFileIfNotTrace(File fileToBeDeleted) {
        if (!log.isTraceEnabled()) {
            FileUtils.deleteQuietly(fileToBeDeleted);
        }
    }

    public static String addBeforeExtension(String file, String whatToAdd) {

        return FilenameUtils.removeExtension(file) + whatToAdd + "." + FilenameUtils.getExtension(file);
    }
}
