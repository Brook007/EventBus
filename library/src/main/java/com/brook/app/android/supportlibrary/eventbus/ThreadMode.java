/*
 * Copyright (c) 2016-present, Brook007 Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
