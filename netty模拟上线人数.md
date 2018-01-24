# netty模拟上线人数

### 目的
```
1. 测试netty 性能
2. 测试推送瓶颈
```

### 使用的是python 的 websocket 工具

```
python 3.6.x 版本
搜索 pythton websocket 模块，并安装
```


### Python 代码：

```



import threading
import datetime
import time
from websocket import create_connection

print("websocket client test");

#! /usr/bin/env python
# -*- coding:utf-8 -*-

# 获取消息接收
class WsSelectorThread(threading.Thread):  # 继承父类threading.Thread
    wsClient = None;
    wsClientName = None;
    startListen = False;
    def __init__(self ):
        threading.Thread.__init__(self)

    def setWsClient(self , ws):
        self.wsClient = ws;
    def setWsClientName(self , name):
        self.wsClientName = name;

    def startListen(self):
        self.startListen = True;

    def run(self):  # 把要执行的代码写到run函数里面 线程在创建后会直接运行run函数
        while (True):
            if self.wsClient and self.startListen :
                result = ws.recv();
                print("ClientName : '%s' , Received '%s' " % (self.wsClientName , result ))
            print('拉取推送数据....')

for i in range(1, 30000):
    # ws = create_connection("ws://127.0.0.1:38888/" , subprotocols=['index' , 'location' , 'news' ,'stockInfo' ])
    ws = create_connection("ws://10.11.165.113:38888/" , subprotocols=['index' , 'location' , 'news' ,'stockInfo' ])
    print("Sending 'Hello, World'...%d "  % i )
    # 创建新线程
    selectorThread = WsSelectorThread()
    clientName = 'ws客户端_: ';
    clientName = clientName + str(i);
    selectorThread.setWsClientName(clientName);
    selectorThread.setWsClient(ws);
    selectorThread.startListen();
    selectorThread.start()



# 线程保活
class OneThread(threading.Thread):  # 继承父类threading.Thread
    def __init__(self ):
        threading.Thread.__init__(self)

    def run(self):  # 把要执行的代码写到run函数里面 线程在创建后会直接运行run函数

        while (True):
            print('保活....')
            time.sleep(5)

# 创建新线程
thread2 = OneThread()
# 开启线程
thread2.start()


```