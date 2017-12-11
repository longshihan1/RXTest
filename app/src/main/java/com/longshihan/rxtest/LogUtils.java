package com.longshihan.rxtest;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LONGHE001.
 *
 * @time 2017/11/30 0030
 * @des
 * @function
 */

public class LogUtils {
    private static final int MAX_LINNE_BYTE_NUM = 3000;

    public static void i(String TAG, String message) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.i(TAG, tag+"\n-------------\n"+message+"\n-------------\n");
    }

    public static void e(String TAG, String message) {

            Log.e(TAG, message);
    }

    public static void v(String TAG, String message) {
            Log.v(TAG, message);
    }

    public static void d(String TAG, String message) {
            Log.d(TAG, message);
    }

    public static void w(String TAG, String message) {
            Log.w(TAG, message);
    }


    public static void output(String TAG, String message) {
        Log.d("SSS","长度:"+message.length());
        String text = TAG + fomatJson(message);
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.i(TAG, tag+"\n-------------\n"+logContent(text)+"\n-------------\n");
    }

    /**
     * json格式化
     *
     * @param jsonStr
     * @return
     */
    private static String fomatJson(String jsonStr) {
        try {
            jsonStr = jsonStr.trim();
            if (jsonStr.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                return jsonObject.toString(2);
            }
            if (jsonStr.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonStr);
                return jsonArray.toString(2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Json格式有误: " + jsonStr;
    }

    static  StringBuilder stringBuilder;
    @NonNull
    private static String generateTag(StackTraceElement caller) {
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        String tag = callerClazzName+"."+caller.getMethodName()+ "("+callerClazzName+":"+caller.getLineNumber()+")"; // 替换
        // tag = TextUtils.isEmpty(tagStr) ? tag : tagStr + ":" + tag;
        StackTraceElement logStackTrace = getLogStackTrace();
        if (stringBuilder!=null){
            stringBuilder.delete(0,stringBuilder.length());
        }else {
            stringBuilder = new StringBuilder();
        }
        stringBuilder.append("Thread: " + Thread.currentThread().getName())
                .append("\n")
                .append(logStackTrace.getClassName()+":"+logStackTrace.getMethodName()+"   ")
                .append(" (").append(logStackTrace.getFileName())
                .append(":").append(logStackTrace.getLineNumber() + ")");
        return stringBuilder.toString();
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }


    private static StackTraceElement getLogStackTrace() {
        StackTraceElement logTackTraces = null;
        StackTraceElement[] stackTraces = Thread.currentThread()
                .getStackTrace();
        // Log.i(logtag, JSONSerializer.toJson(stackTraces));
        for (int i = 0; i < stackTraces.length; i++) {
            StackTraceElement stackTrace = stackTraces[i];
            if (stackTrace.getClassName().equals(LogUtils.class.getName())) {
                logTackTraces = stackTraces[i + 3];
                i = stackTraces.length;
            }
        }
        return logTackTraces;
    }

    /**
     * 打印内容
     *
     * @param text
     * @return
     */
    private static String logContent(String text) {
        if (text.length() < 50) {// 内容长度不超过50，前面加空格加到50
            int minLeng = 50 - text.length();
            // Log.i(logtag, "leng========" + leng + "   " + text.length());
            if (minLeng > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < minLeng; i++) {
                    stringBuilder.append(" ");
                }
                text = text + stringBuilder.toString();
            }
        } else if (text.length() > 1048) {// 内容超过logcat单条打印上限，分批打印
            //Log.i(logtag, "text长度=========" + text.length());
            int logTime = text.length() / 1048;
            for (int i = 0; i < logTime; i++) {
                String leng = text.substring(i * 1048, (i + 1) * 1048);
                // 提示
                if (i == 0) {
                    Log.i("", "打印分" + logTime + "条显示 :" + leng);
                } else {
                    Log.i("", "接上条↑" + leng);
                }

            }
            text = "接上条↑"
                    + text.substring(logTime * 1024, text.length());
        }
        return text;
    }





    public static boolean isLoggable(String TAG) {
        return Log.isLoggable(TAG, Log.INFO);
    }
}
