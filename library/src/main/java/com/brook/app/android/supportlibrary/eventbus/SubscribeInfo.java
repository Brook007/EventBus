package com.brook.app.android.supportlibrary.eventbus;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 保存一个接收订阅消息的方法的信息
 *
 * @auther Brook
 * @time 2017/5/26 21:42
 */
public class SubscribeInfo {

    // 一个接收方法的缓存
    private Method method;

    // 当前接收方法的参数
    private Class[] parameter;

    // 保存所有关心的发布者的全名，Set集合保证名字唯一
    private Set<String> filter;

    // 当前接收者回调的线程模式
    private int threadMode;

    public int getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(int threadMode) {
        this.threadMode = threadMode;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class[] getParameter() {
        return parameter;
    }

    public void setParameter(Class[] parameter) {
        this.parameter = parameter;
    }

    public Set<String> getFilter() {
        return filter;
    }

    public void setFilter(Set<String> filter) {
        this.filter = filter;
    }
}
