# AppBridge接入文档

------

接入步骤：

> * Client端App和HostService端App集成 bridgemodule aar
> * Client端App初始化及接口使用
> * HostService端App初始化及接口使用

------





### 1. Client端App和HostService端App集成 bridgemodule aar
配置应用目录下的build.gradle
1.1 build.gradle下添加
&nbsp;&nbsp;&nbsp;&nbsp;repositories {
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;flatDir {
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dirs 'libs'
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}


1.2 dependencies下添加：compile(name: 'bridgemodule-v1.x', ext: 'aar')
1.3 修改ProcessHelper.REAL_PACKAGE_NAME修改为自己项目（主app，需要协同其他app完成工作）的包名，并打包aar
1.4 将bridgemodule-v1.x文件copy在项目libs目录下



### 2.  Client端App初始化及接口使用
2.1 初始化两种支持两种：
&nbsp;第一种：应用的Application集成DispatchBaseApp
&nbsp;第二种：在应用的Application onCreate中调用ProcessHelper.init(new ProcessHelperConfig(this));



2.2 接口使用：
 &nbsp;&nbsp;2.2.1:&nbsp;发送消息给小程序宿主：DSClient.getInstance().send(msg);&nbsp; //msg的消息格式由小程序数组和第三方约定;


&nbsp;&nbsp;2.2.2:&nbsp;接收来自小程序宿主的消息,&nbsp;&nbsp;***建议：此注册放在Application中以便拉起非运行状态的三方app可即时接收到消息：***
```
IMessageCallback messageCallback = new IMessageCallback.Stub() {
            @Override
            public void onReceiveMessage(String msg) throws RemoteException {
                if (DEBUG) {
                    Log.d(TAG, "onReceiveMessage 处理来自小程序的msg: " + msg);
                }
            }
        };
DSClient.getInstance().registerMessageCallback(messageCallback);
```
### 3.  HostService端App初始化及接口使用
3.1 初始化：
&nbsp;在应用的Application onCreate中调用
```
ProcessHelper.init(new ProcessHelperConfig(this));
ProcessHelper.registerService(ProcessHelper.PROCESS_HOST_MSG,
                ProcessHelper.HOST_MSG_SERVICE, DispatchServiceStub.class);
```

3.2 拷贝hostapp工程的service包到自己的项目：
 &nbsp;&nbsp;3.2.1:&nbsp;参考hostapp在manifest文件中注册
```
 <provider
    android:name=".service.MessageDispatchProvider"
    android:authorities="${applicationId}"
    android:exported="true" />
```


&nbsp;&nbsp;3.2.2:&nbsp;收发消息参考MessageDispatcher，根据业务拓展逻辑。

[messagedispatch_v1.2.1.aar](https://ecloud.baidu.com?t=190b314c3fe3596f67b95922571a0e31)

ps：文档和代码不完善之处后续有时间再补充！

------

Nothing is final，有任何问题，欢迎反馈！