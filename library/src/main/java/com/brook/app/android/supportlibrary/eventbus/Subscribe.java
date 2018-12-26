package com.brook.app.android.supportlibrary.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识需要接收消息的回调方法
 *
 * @auther Brook
 * @time 2017/5/26 22:04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    /**
     * 指定回调在哪一种线程中执行
     *
     * @return
     */
    @ThreadMode.Mode int threadMode() default ThreadMode.POSTING;

    /**
     * 发送者过滤器，表示只关心列表中的发送者发出的消息，不设置时，表示接收所有符合条件的消息
     *
     * @return
     */
    Class[] filter() default {};
}
