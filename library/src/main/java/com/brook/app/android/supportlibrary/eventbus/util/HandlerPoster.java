package com.brook.app.android.supportlibrary.eventbus.util;

import android.os.Handler;
import android.os.Looper;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @auther Brook
 * @time 2017/5/27 9:54
 */
public class HandlerPoster extends Handler {

    private Queue<Runnable> postQueue;

    private volatile boolean inPosting = false;

    public HandlerPoster(Looper looper) {
        super(looper);
        postQueue = new ConcurrentLinkedQueue<>();
    }

    public void enqueue(Runnable runnable) {
        postQueue.offer(runnable);
        if (!inPosting) {
            Util.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    inPosting = true;
                    Runnable peek = postQueue.poll();
                    while (peek != null) {
                        HandlerPoster.this.post(peek);
                        peek = postQueue.poll();
                    }
                    inPosting = false;
                }
            });
        }
    }

}
