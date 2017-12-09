# Netty实现的websocket推送框架

### 2017-11-20 更新 单请求多协议(多订阅) 实现

# WebSocket Netty实现

### 目的
业务需求，需要向前端浏览器订阅推送业务，接受后端推送，之前用的是amq.js （activemq基于轮询实现），有很大的性能问题和实时性也无法保证；
所以就使用了 netty 实现了个 Websocket 框架

推送需求：有时候需要进行多订阅，对于前端的需求的多消息类型分别接收，后端需要不同订阅的业务进行隔离，各自发送推送，所以又优化了单请求多订阅的功能。

#### WebSocket 基于H5实现，低版本IE不支持 (ie8)

#### 有时候需要网页和手机端都要一致推送协议，则手机客户端也可能需要实现websocket推送


[WebSocket协议（RFC6455）的翻译和官网描述地址]
websocket浏览器h5对象，前端api文档 ： https://www.w3.org/TR/websockets/


### 项目目录以及环境配合

#### 因为是个测试项目，所以可能会有别的框架代码，但是不会影响框架环境，也证明了这个框架是0侵入性的，拷进去就能用


### 源码说明
#### Java代码都在 websocket 目录下，
#### spring 配置在 spring-netty-websocket.xml
#### html 测试页面在 html/static 中
     1. websocket-push-pl.html 页面是单请求单订阅(单处理器)的实现
     2. websocket-multi-sub.html 页面是单请求多订阅的实现



### Java 代码
```
1. server 包
  netty 的serverBootstrap 的启动与端口监听类
2. handler 包
 netty 的channelHandler的实现，主要实现了(通过工厂模式) 用来处理websocket 的请求(http升级，握手，以及对于请求处理)
具体处理逻辑在：WebSocketChannelHandler.java 的 channelRead() 方法中
调用 upgradeResolver 处理http 升级请求，可以处理 uri ，过滤拦截，异常处理

调用 requestHandlerMapping 获取对应uri 的 HandlerAdapter 请求处理类

webSocketCacheManager 整个netty 框架中存储和保活的存储层，里面封装了一层dao层，做好存储框架替换的准备



3. adapter 包
websocket请求握手成功后，服务端和客户端的通信是基于frame 的(data frame 和 control frame)

adapter 包中定义了 HandlerAdapter 接口，三个方法

/* 用来处理客户端发送的数据 */
handleRequest();

/** 服务端处理(或者是推送处理)或者是聊天业务中获取目标对象的id 查找到对应的 HandlerAdapter 和 channel 进行推送聊天消息 **/
handleResponse();

/** 连接完成时调用* */
onUpgradeCompleted();

该接口我实现了2个抽象父类
一. 用来处理control frame 的AbstractFrameHandlerAdapter 因为毕竟大部分的control frame 的处理都是一样的，可以继承它，或者对于特定的frame可以自己重写方法实现

二. KeepAliveHandlerAdapter 用来处理保活，心跳机制的处理类，同上，也是因为几乎所有用websocket的业务都需要这样的处理，所以也可以封装成父类进行实现，具体保活机制在类的注释上



======= 这里优化了一个处理类
SubprotocolHandlerAdapter.java 用来处理单请求多协议的业务实现
子协议处理类(实现 WSProtocolHandler.java / AbstractProtocolHandler.java )，通过配置注解 @WSProtocol 子协议，和父 uri(默认是/ ) 可以绑定一个请求多个子协议的处理，  SubprotocolHandlerAdapter.java 的处理则是将客户端订阅的protocol 与对应的 WSProtocolHandler 关联，前端的推送能映射到相应的子协议处理器，后端的推送能推给订阅的客户端

4. mapping 包
WSRequestHandlerMapping.java 封装了处理单uri 请求和 带子协议的请求映射的处理器

5. chat 和 common 包
这两个包下的实现类，并不属于框架源码，是作者用来测试不同业务的实现
chat 包下是实现聊天业务的实现
common 包下两个类实现了不同的推送订阅的处理
protocols 包下的 WSProtocolHandler.java 实现类都是对子协议的实现

6.resolver 包
一些消息类型的处理，升级websocket 请求的处理类 UpgradeResolver.java


7.WSProtocolHandler.java / AbstractProtocolHandler.java 主要处理子协议
封装推送消息
public String wrapperPushedMessage(Map<String , Object> params);
接收到客户端消息
public void onMessageRecieved(ChannelHandlerContext ctx , JSONObject message);
连接建立事件
public void onProtocolRegistyCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient);



```

#### 源码目录

该项目是基于netty + spring + maven 的
jdk1.8

#### netty maven地址：
```
        <!-- https://mvnrepository.com/artifact/io.netty/netty-all -->
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.11.Final</version>
        </dependency>

```

项目中有个 spring 容器，只需要在应用(或者web 应用)中的spring 上下文引入websocket 的配置即可启动:

```
	<import resource="./spring-netty-websocket.xml"/>

```

#### 源码位置：
```
所有源码都在这个包下
com.dragsun.websocket

注意：
源码包中的
chat 与 common 包，是具体业务的实现！！(example)

还有个测试的service类，用来测试推送业务，即开启定时器，向订阅的客户端发送对应的推送内容 WebSocketTestService.java

com.java.service.WebSocketTestService.java
```


#### spring-netty-websocket.xml 文件就是 netty-websocket的实现，其中对于websocket 网络请求的操作业务类，都由spring 容器进行管理


### 业务示例
当前浏览器页面(或者android / ios)上遇到的需要使用到 websocket 的业务
1.聊天
2.推送信息

项目中也大致的实现了这些功能
#### 聊天

```
com.java.core.netty.websocket.chat 包下

ChatHandlerAdapter.java 类用来实现聊天业务：
前端发送对方的id （可以是channelId ，或者与channelId关联的任何key ,最后都要通过channelId 找到具体的对方也就是接收方的channel ，发送聊天消息）

ChatOnlineListHandlerAdapter.java 类用来实现，聊天人员的列表推送，使得前端有实时的在线人员进行选择，发送数据

当然这两个业务放在同一个处理类下进行处理是没有问题的


```


#### 推送
```
例子：

com.java.service.WebSocketTestService.java 中
的
frameHandlerAdapter.handleResponse(frameParams);
和
locationHandlerAdapter.handleResponse(locParams);
用来推送服务端消息

子协议推送：WSProtocolHandler.java
例子
IndexProtocolHandler protocolHandler = applicationContext.getBean(IndexProtocolHandler.class);
Map<String , Object> frameParams = new HashMap();
String frameMessage = "我是 IndexProtocolHandler 业务推送的数据" + System.currentTimeMillis();
frameParams.put("message" , frameMessage);
protocolHandler.pushMessage(frameParams);


```

本来项目


### 安全

#### netty 中实现websocket 权限

因为很多应用都是部署在tomcat 用tomcat的过滤器进行权限验证

而 netty 服务是监听另一个端口(如38888)则会存在安全访问问题

1. 首先服务器设置orign 请求头

#### 方案1：
请求 websocket 前可以先通过 ajax
 请求，后台登陆权限成功通过生成一个 key ,返回前端，前端发起 websocket 请求带上这个token，然后后台就可以验证身份了

#### 方案2：
反向代理
在web.xml 中配置一个自定义的Filter 在权限验证后，判断是否 websocket请求，然后对请求进行反向代理
此方案难度太大，曾经使用nginx+lua 模块实现，最后还是不行，网上有通过java实现的反向代理技术，可以试试


### 测试文件
```
websocket-chat-test.html
websocket-push-pl.html

实际效果图见—> 演示效果 文件夹
```



单请求多订阅前端实现：
```
/*
protocols 是数组，可以传多个
*/
var webSocketClient = new WebSocketClient(url ,  protocols );


// 例子
var webSocketClient = new WebSocketClient('ws://127.0.0.1/' ,  ['index' , 'stockInfo'] );
// 页面 
websocket-multi-sub.html

```
