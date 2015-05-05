package com.lvideo.util;

import android.util.Log;

public class AppLog
{
    private static boolean showLog = false;

    /**
     * @param enable
     */
    public static void enableLogging(boolean enable)
    {
        showLog = enable;
    }

    /**
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg)
    {
        if (showLog)
            Log.i(tag, msg);
    }

    /**
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg)
    {
        if (showLog)
            Log.e(tag , msg);
    }
    
    public static void e(String msg)
    {
    	  if (showLog)
              Log.e("jwzhangjie" , msg);
    }
    
    /**
     * @param tag
     * @param msg
     * @param tr
     */
    public static void e(String tag, String msg, Throwable tr)
    {
        if (showLog)
            Log.e(tag , msg, tr);
    }

    /**
     * @param tag
     * @param msg
     */
    public static void w(String tag , String msg)
    {
        if (showLog)
            Log.w(tag, msg);
    }
    
    /**
     * @param tag
     * @param msg
     */
    public static void w(String tag , String msg, Throwable tr)
    {
        if (showLog)
            Log.w(tag, msg, tr);
    }

    /**
     * @param tag
     * @param msg
     */
    public static void d(String tag , String msg)
    {
        if (showLog)
            Log.d(tag, msg);
    }

    /**
     * @param tag
     * @param msg
     */
    public static void v(String tag , String msg)
    {
        if (showLog)
            Log.v(tag, msg);
    }
}
