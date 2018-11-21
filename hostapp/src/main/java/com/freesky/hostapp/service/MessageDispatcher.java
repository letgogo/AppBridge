package com.freesky.hostapp.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.freesky.appbridge.ProcessHelperConfig;
import com.freesky.appbridge.service.IMessageCallback;
import com.freesky.hostapp.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by letgogo on 2018/11/20.
 * 消息分发器
 */

public class MessageDispatcher {

    private static final String TAG = MessageDispatcher.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private HashMap<String, MessageRemoteCallback> mMessageCallbacks = new HashMap<>();
    private List<Message> mMessageQueue = new ArrayList<Message>();
    private Context mContext;

    private MessageDispatcher() {
        mContext = ProcessHelperConfig.getAppContext();
    }

    public static MessageDispatcher getInstance() {
        return SingleHolder.DISPATHER;
    }

    private static class SingleHolder {
        private static MessageDispatcher DISPATHER = new MessageDispatcher();
    }

    public void registerMessageCallback(String pkg,
                                        IMessageCallback messageCallback)
            throws RemoteException {
        if (DEBUG) {
            Log.d(TAG, "registerMessageCallback pkg: "
                    + pkg
                    + ", messageCallback: "
                    + messageCallback);
        }
        if (TextUtils.isEmpty(pkg)) {
            return;
        }
        // TODO 如有必要，添加校验请求合法性逻辑
        MessageRemoteCallback callbackList = new MessageRemoteCallback();
        callbackList.register(messageCallback, pkg);
        mMessageCallbacks.put(pkg, callbackList);
        Iterator iterator = mMessageQueue.iterator();
        while (iterator.hasNext()) {
            Message message = (Message) iterator.next();
            if (pkg.equals(message.pkg)) {
                dispatchMessage(message);
                iterator.remove();
            }
        }
    }

    public void dispatchMessage(Message message) {
        /**
         * TODO
         * 1.根据业务进行消息分发，定义消息协议，如JSON
         * 2.分发前判断目标应用是否已经运行，如果为运行，则先将消息加入消息队列，并尝试拉起应用
         */
        if (message == null) {
            return;
        }
        RemoteCallbackList<IMessageCallback> callbackList = mMessageCallbacks.get(message.pkg);

        if (DEBUG) {
            Log.d(TAG, "dispatchMessage callbackList: " + callbackList);
        }
        if (callbackList == null) {
            if (DEBUG) {
                Log.d(TAG, "Client have not register IMessageCallback");
            }
            enqueueMessage(message);
            ComponentName componentName = new ComponentName(message.pkg,
                    "com.freesky.appbridge.WakeupService");
            Intent intent = new Intent();
            intent.setComponent(componentName);
            mContext.startService(intent);
            return;
        }
        int count = callbackList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            IMessageCallback messageCallback = callbackList.getBroadcastItem(i);
            try {
                final String result = messageCallback.onReceiveMessage(message.msg);
                if (DEBUG) {
                    Log.d(TAG, "Client onReceiveMessage result : " + result);
                }
                // remove the under code if needed, only for debug
                if (DEBUG) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        callbackList.finishBroadcast();
    }

    public String handleClientMessage(Message message) {
        /**
         * TODO
         * 处理客户端发送过来的消息，可配合其他业务接口完成消息逻辑
         */
        return "来自HostApp服务端的result";
    }

    private void enqueueMessage(Message message) {
        if (message == null) {
            return;
        }
        if (mMessageQueue.contains(message)) {
            return;
        }
        mMessageQueue.add(message);
    }

    private class MessageRemoteCallback extends RemoteCallbackList<IMessageCallback> {
        @Override
        public void onCallbackDied(IMessageCallback callback, Object cookie) {
            super.onCallbackDied(callback);
            try {
                String pkg = (String) cookie;
                mMessageCallbacks.remove(pkg);
            } catch (ClassCastException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

}
