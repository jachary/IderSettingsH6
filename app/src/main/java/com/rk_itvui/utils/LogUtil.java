package com.rk_itvui.utils;

import android.util.Log;

/**
 * Created by guoliang.wgl on 18/3/18.
 */
public class LogUtil {
    public static String TAG = "Setting2";
    public void LOGD(String subTag,String text){
        Log.d(TAG,subTag+ ":"+text);
    }
}
