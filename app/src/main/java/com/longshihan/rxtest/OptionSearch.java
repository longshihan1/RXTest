package com.longshihan.rxtest;

import android.os.Looper;
import android.os.Message;

/**
 * Created by LONGHE001.
 *
 * @time 2017/12/4 0004
 * @des 优化即时搜索
 * @function 思想：延迟发送，避免出现粘性请求
 */

public class OptionSearch implements CommonHandler.MessageHandler {
    private String currentWord;
    private EditFinishListener mListener;

    private MyRunnable myRunnable = new MyRunnable();

    private CommonHandler mHandler;

    /**
     * EditText自响应间隔时间（ms）
     * 默认400ms
     */
    private int INTERVAL_TIME = 400;

    public OptionSearch(Looper looper) {
        this(looper, 0);
    }

    public OptionSearch(Looper looper,int intevalTime) {
        if (intevalTime==0){
            INTERVAL_TIME=400;
        }else {
            INTERVAL_TIME=intevalTime;
        }
        mHandler = new CommonHandler(looper, this);
    }

    public void optionSearch(String keyword) {
        this.currentWord = keyword;
        if (myRunnable != null) {
            mHandler.removeCallbacks(myRunnable);
        }
        mHandler.postDelayed(myRunnable, INTERVAL_TIME);
    }

    public void setListener(EditFinishListener listener) {
        this.mListener = listener;
    }

    @Override
    public void handleMessage(Message msg) {
        if(mListener!=null){
            mListener.getKeyword(currentWord);
        }
    }


    //接口，接受回调数据
    public interface EditFinishListener {
        void getKeyword(String keyword);
    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(1);
        }
    }
}
