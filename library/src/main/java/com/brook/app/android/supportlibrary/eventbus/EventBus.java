package com.brook.app.android.supportlibrary.eventbus;

import android.os.Looper;
import android.util.Pair;


import com.brook.app.android.supportlibrary.eventbus.util.HandlerPoster;
import com.brook.app.android.supportlibrary.eventbus.util.Util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @auther Brook
 * @time 2017/5/26 21:33
 */
public class EventBus {

    // 记录所有发布者的消息
    private volatile Map<Object, List<SubscribeInfo>> map;

    // 用于存储粘性事件
    private volatile List<Pair<String, Object[]>> sticky;

    // Android主线程事件处理器，需要在主线程中回调的方法都会通过他来处理
    private HandlerPoster mHandler;

    // 默认的线程池，用于执行接收者方法
    private ExecutorService sExecutorService;

    /**
     * 默认构造方法，执行相关容器的初始化
     */
    public EventBus() {
        mHandler = new HandlerPoster(Looper.getMainLooper());
        sExecutorService = Util.getThreadPool();
        map = new WeakHashMap<>();
        sticky = new LinkedList<>();
    }

    /**
     * 获取默认的单例
     *
     * @return 得到默认的单例
     */
    public static EventBus getDefault() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 静态类单例模式
     */
    private static class SingletonHolder {
        private static final EventBus INSTANCE = new EventBus();
    }

    /**
     * 设置默认的线程池
     *
     * @param executorService 需要指定的线程池
     */
    public void setExecutorService(ExecutorService executorService) {
        if (executorService == null) {
            return;
        }
        sExecutorService = executorService;
    }

    /**
     * 注册一个订阅者，如果当前订阅者不包含任何接收订阅消息(@Subscribe)的方法，则当前订阅者不会注册成功
     *
     * @param subscribe
     * @return
     */
    public EventBus register(final Object subscribe) {
        synchronized (this) {
            if (!map.containsKey(subscribe)) {
                List<SubscribeInfo> subscribeInfoList = new ArrayList<>();

                Class<?> subscribeClass = subscribe.getClass();
                Method[] declaredMethods = subscribeClass.getMethods();
                SubscribeInfo subscribeInfo;
                for (final Method method : declaredMethods) {
                    Subscribe annotation = method.getAnnotation(Subscribe.class);
                    if (annotation != null) {
                        subscribeInfo = new SubscribeInfo();
                        int threadMode = annotation.threadMode();
                        Class[] filter = annotation.filter();
                        subscribeInfo.setMethod(method);
                        subscribeInfo.setFilter(Util.toList(filter));
                        subscribeInfo.setParameter(method.getParameterTypes());
                        subscribeInfo.setThreadMode(threadMode);
                        subscribeInfoList.add(subscribeInfo);
                        // 粘滞事件
                        Class[] parameter = subscribeInfo.getParameter();

                        for (Pair pair : sticky) {
                            if (subscribeInfo.getFilter().isEmpty()
                                    || subscribeInfo.getFilter().contains(pair.first)) {
                                final Object[] second = Arrays.copyOf((Object[]) pair.second, ((Object[]) pair.second).length);
                                if (Util.compare(parameter, second)) {
                                    switch (subscribeInfo.getThreadMode()) {
                                        case ThreadMode.POSTING:
                                            // 直接执行
                                            if (second.length == 0) {
                                                try {
                                                    subscribeInfo.getMethod().invoke(subscribe);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                try {
                                                    subscribeInfo.getMethod().invoke(subscribe, second);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            break;
                                        case ThreadMode.ASYNC:
                                            // 异步执行
                                            if (second.length == 0) {
                                                sExecutorService.execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            method.invoke(subscribe);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } else {
                                                sExecutorService.execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            method.invoke(subscribe, second);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }

                                            break;
                                        case ThreadMode.MAIN:
                                            // UI执行
                                            if (second.length == 0) {
                                                mHandler.enqueue(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            method.invoke(subscribe);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } else {
                                                mHandler.enqueue(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            method.invoke(subscribe, second);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }

                                            break;
                                    }
                                }
                            }
                        }

                        map.put(subscribe, subscribeInfoList);
                    }
                }
            }
        }
        return this;
    }

    /**
     * 移除一个事件订阅者
     *
     * @param object 需要移除的订阅者
     */
    public synchronized void unregister(Object object) {
        map.remove(object);
    }


    /**
     * 发布一个事件
     *
     * @param event 参数为不定参数， null参数为通配符，会匹配所有的类型。
     *              </p>
     *              例如：post(null); 那么所有的receiver(Object arg);包含有一个参数的事件接收者都会收到消息，除非接收者不关心当前发送者发布的消息。
     *              post(Object, null); 会通知所有的receiver(Object, int); receiver(Object, String);在其他参数相同的情况下，null相当于通配符。
     *              post();表示发布一个空消息，所有的receiver();没有参数的接收者都会接受到消息，除非接收者不关心当前发送者发布的消息。
     * @return
     */
    public EventBus post(final Object... event) {
        if (event != null) {
            synchronized (this) {
                Iterator<Map.Entry<Object, List<SubscribeInfo>>> iterator = map.entrySet().iterator();
                String whoCallMe = Util.getWhoCallMe();
                while (iterator.hasNext()) {
                    Map.Entry<Object, List<SubscribeInfo>> entry = iterator.next();
                    final Object object = entry.getKey();
                    if (object == null) {
                        // 移除已经死亡的订阅者, 并跳过后续步骤
                        iterator.remove();
                        continue;
                    }
                    // 当前订阅者的全部的能够接收订阅消息的方法
                    List<SubscribeInfo> value = entry.getValue();
                    for (final SubscribeInfo subscribeInfo : value) {
                        Set<String> filter = subscribeInfo.getFilter();
                        if (filter.isEmpty() || filter.contains(whoCallMe)) {
                            Class[] parameter = subscribeInfo.getParameter();
                            final Object[] objects = Arrays.copyOf(event, event.length);
                            if (Util.compare(parameter, objects)) {
                                switch (subscribeInfo.getThreadMode()) {
                                    case ThreadMode.POSTING:
                                        // 直接执行
                                        if (objects.length == 0) {
                                            try {
                                                subscribeInfo.getMethod().invoke(object);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            try {
                                                subscribeInfo.getMethod().invoke(object, objects);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    case ThreadMode.ASYNC:
                                        // 异步执行
                                        if (objects.length == 0) {
                                            sExecutorService.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        subscribeInfo.getMethod().invoke(object);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } else {
                                            sExecutorService.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        subscribeInfo.getMethod().invoke(object, objects);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                        break;
                                    case ThreadMode.MAIN:
                                        // UI执行
                                        if (objects.length == 0) {
                                            mHandler.enqueue(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        subscribeInfo.getMethod().invoke(object);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } else {
                                            mHandler.enqueue(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        subscribeInfo.getMethod().invoke(object, objects);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return this;
    }

    /**
     * 发布一个粘滞事件，发布的同时，会把当前事件通知所有需要接收当前事件的订阅者
     *
     * @param event 事件消息
     * @return
     */
    public EventBus postSticky(Object... event) {
        sticky.add(new Pair<String, Object[]>(Util.getWhoCallMe(), event));
        return post(event);
    }

    /**
     * 移除所有订阅者
     */
    public void removeAllSubscribe() {
        map.clear();
    }

    /**
     * 移除指定类型的粘滞事件
     *
     * @param event 需要移除的事件
     */
    public void removeStickyEvent(Object... event) {
        Iterator<Pair<String, Object[]>> iterator = sticky.iterator();
        while (iterator.hasNext()) {
            Pair<String, Object[]> next = iterator.next();
            if (Util.compareTo(next.second, event)) {
                iterator.remove();
            }
        }
    }
}
