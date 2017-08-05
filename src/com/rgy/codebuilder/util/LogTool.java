package com.rgy.codebuilder.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/8/5.
 */
public class LogTool {

    public static void writeException(Exception e){
        e.printStackTrace();
        Logger log = Logger.getLogger("codebuilder");
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("log/exception.log", true);
            log.addHandler(fileHandler);
            log.info(e.getMessage());
        } catch (IOException e1) {
            e1.printStackTrace();
        }finally {
            if(fileHandler!=null){
                fileHandler.close();
            }
        }

    }
}
