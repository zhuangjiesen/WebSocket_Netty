# Netty实现的websocket推送框架

### 2018-05-24 更新 

### 主要改动
```
1. 冗余代码和过度设计的优化
2. 基于spring-websocket框架，重新设计的
3. 开放 com.dragsun.websocket.handler.websocket.AbstractWebSocketHandler 与 com.dragsun.websocket.handler.websocket.WebSocketHandler 接口，自己根据业务需求去实现
4. 分模块，便于接入自己的项目
5.//TODO 添加控制台和统计监控
```

### 结构
    ```
    基于spring 容器
    - 项目新增springboot 的启动，用于一些web操作
    - 有直接通过App.java 启动程序    
    - 后续会添加 web 控制台，监控连接数和统计面板 (dashboard)
    - 划分了模块 ：
        dragsun-main 作为程序入口
            resources: spring、springboot 配置文件
            templates: 静态页面和资源文件夹以及测试网页
        websocket-server websocket实现模块
        以及其他模块(bing 微软必应SpeechToText的接入)
    ``` 

# WebSocket Netty实现

### 目的
业务需求，需要向前端浏览器订阅推送业务，接受后端推送，之前用的是amq.js （activemq基于轮询实现），有很大的性能问题和实时性也无法保证；
所以就使用了 netty 实现了个 Websocket 框架

推送需求：有时候需要进行多订阅，对于前端的需求的多消息类型分别接收，后端需要不同订阅的业务进行隔离，各自发送推送，所以又优化了单请求多订阅的功能。

#### WebSocket 基于H5实现，低版本IE不支持 (ie8)

#### 有时候需要网页和手机端都要一致推送协议，则手机客户端也可能需要实现websocket推送


[WebSocket协议（RFC6455）的翻译和官网描述地址](https://github.com/zhuangjiesen/reading-learning-coding/blob/master/网络协议/RFC6455%20WebSocket协议.md)

websocket浏览器h5对象，前端api文档 ： https://www.w3.org/TR/websockets/

### 源码与配置

#### 程序入口在 dragsun-main 模块下
#### 在 dragsun-main 模块中的 resources/websocket.xml 的spring 配置文件中配置
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:util="http://www.springframework.org/schema/util"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
            http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd    
            http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-3.0.xsd  
http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.2.xsd            http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-3.0.xsd"
>


    <!--<bean id="webSocketHandler" class="com.jason.websocket.speech.bing.BingWebSocketHandler"></bean>-->
    <!--<bean id="webSocketHandler" class="com.dragsun.speech.google.GoogleWebSocketHandler"></bean>-->

    <!--默认处理器-->
    <bean id="webSocketHandler" class="com.jason.websocket.common.SimpleWebSocketHandler"></bean>
    <!--配置-->
    <bean id="wSConfiguration" class="com.dragsun.websocket.server.Configuration">
        <property name="webSocketHandler" ref="webSocketHandler"/>
    </bean>
    <bean id="webSocketNettyServer" class="com.dragsun.websocket.server.WebSocketNettyServer" >
        <property name="port" value="38888" />
        <property name="configuration" ref="wSConfiguration"/>
    </bean>


</beans>


```



#### Netty 的Server 处理类在 com.dragsun.websocket.server.WebSocketNettyServer
#### WebSocket的请求封装与握手，以及心跳机制在类 com.dragsun.websocket.handler.WebSocketChannelHandlerFactory 

