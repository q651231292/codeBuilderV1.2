package com.rgy.codebuilder.util;

/**
 * Created by Administrator on 2017/8/26.
 */
public class ValiTool {

    public static boolean strIsNull(String str){

        if(str!=null){
            return true;
        }
        if(str.trim().equals("")){
            return true;
        }

        return false;
    }
}
