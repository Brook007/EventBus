package com.brook.app.android.supportlibrary.eventbus;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于设置线程回调的执行线程
 *
 * @auther Brook
 * @time 2017/5/26 22:04
 */
public class ThreadMode {

    @IntDef({MAIN, ASYNC, POSTING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }



    /**
     * 在安卓的UI线程中执行
     */
    public final static int MAIN = 0;

    /**
     * 在异步线程中执行回调
     */
    public final static int ASYNC = 1;

    /**
     * 在同步线程中执行回调，谁调用就在谁的线程中执行
     */
    public final static int POSTING = 2;
}
