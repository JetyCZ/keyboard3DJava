package net.jetensky.keyboard3djava.imgprocessor;

import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import net.jetensky.keyboard3djava.util.FileSystemUtil;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class ImgProcessorPipeline extends ArrayList<ImgProcessor> {

    private final static Logger log = LoggerFactory.getLogger(ImgProcessorPipeline.class);

    private String name;
    private Mat startingImage;

    public ImgProcessorPipeline(String name) {
        this.name = name;
    }

    public Img process(Img startingImage) {
        this.startingImage = startingImage.getMat();
        FileSystemUtil.writeTraceImage(startingImage.getFolder(),name + "_1-initial", startingImage.getMat());
        int index = 1;
        Img toProcess = startingImage;

        log.debug("Debug image folder: " + startingImage.getFolder());

        for (ImgProcessor imgProcessor : this) {
            index ++;
            toProcess = imgProcessor.process(toProcess);
            Mat outputMat = toProcess.getMat();
            FileSystemUtil.writeTraceImage(startingImage.getFolder(), name + "_" + index + "-" + imgProcessor.getClass().getSimpleName(), outputMat);
            imgProcessor.setMatOutput(outputMat);

        }
        return toProcess;
    }

    public MatProvider getStartingImage() {
        return new MatProvider() {
            @Override
            public Mat matOutput() {
                return startingImage;
            }

            @Override
            public Mat toProcess() {
                return startingImage;
            }
        };
    }


    /**
     * Releases outputMats for all processors in pipeline.
     * Note: Does not release:
     *  1) source mat (stored in startingImage),
     *  2) output mat (Img.getMat())
     * Its up to client code that provided source mat.
     * @param releaseOutputMat forces to release outputMat
     */
    public void release(boolean releaseOutputMat) {
        int imgProcessorIndex = 0;
        for (ImgProcessor imgProcessor: this) {

            if(imgProcessor.toProcess()!=null) {
                // We are not allowed to release original (source) Mat which was passed to process method and outputMat
                if (imgProcessorIndex>0) {
                    imgProcessor.toProcess().release();
                }
            } else {
                log.debug("Couldn't release input Mat for " + (imgProcessorIndex+1) +
                        ". ImgProcessor " + imgProcessor.getClass().getName());
            }
            boolean last = imgProcessorIndex == (size()-1);
            // We are not allowed to release outputMat of last processor unless forced with releaseOutputMat
            if (!last || releaseOutputMat) {
                Mat matOutput = imgProcessor.matOutput();
                if (matOutput!=null) matOutput.release();
            } else {
                log.debug("Did not release output Mat for " + (imgProcessorIndex+1)+
                        ". ImgProcessor "+imgProcessor.getClass().getName());
            }

            imgProcessorIndex++;
        }
    }

    @Override
    public boolean add(ImgProcessor imgProcessor) {
        ((AbstractImgProcessor) imgProcessor).setPipeline(this);
        return super.add(imgProcessor);
    }
}
