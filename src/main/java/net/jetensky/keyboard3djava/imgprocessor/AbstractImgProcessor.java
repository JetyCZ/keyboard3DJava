package net.jetensky.keyboard3djava.imgprocessor;


import net.jetensky.keyboard3djava.imgprocessor.dto.Img;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractImgProcessor implements ImgProcessor, MatProvider {
    protected static Logger log = LoggerFactory.getLogger(AbstractImgProcessor.class);

    protected Mat matOutput;
    protected Mat toProcess;
    private ImgProcessorPipeline pipeline;

    public void setMatOutput(Mat matOutput) {
        this.matOutput = matOutput;
    }

    public void setToProcess(Mat toProcess) {
        this.toProcess = toProcess;
    }

    @Override
    public abstract Img process(Img img);

    @Override
    public Mat matOutput() {
        return this.matOutput;
    }

    @Override
    public Mat toProcess() {
        return this.toProcess;
    }

    public void setPipeline(ImgProcessorPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public ImgProcessorPipeline getPipeline() {
        return pipeline;
    }
}
