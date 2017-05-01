package net.jetensky.keyboard3djava.util.opencv;

import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpencvLoaderHelper {

    private final static Logger log = LoggerFactory.getLogger(OpencvLoaderHelper.class);
    private static boolean alreadyInit;

    public static boolean initOpenCV() {
        if (alreadyInit) return true;

        String openCVLibPath = "/usr/share/OpenCV/java/";
        String libOpenCV = openCVLibPath + "lib"+ Core.NATIVE_LIBRARY_NAME+".so";
        System.load(libOpenCV);
        log.debug("Loaded native library " + libOpenCV);
        alreadyInit = true;
        return true;
    }


}
