var WebSocketClient = function (url , subprotocols ){
    //请求url
    this.url = url;
    this.name = null;
    this.isOpen = false;
    this.socket =  null;
    //手动关闭
    this.manulClosed = false;

    this.retry = 3;
    //正在初始化
    this.retrying = false;



    // ready state
    var CONNECTING = 0;
    var OPEN = 1;
    var CLOSING = 2;
    var CLOSED = 3;

    //发送方法
    this.send = function (callback) {
        if (callback) {
            callback (false);
        }
    };

    /** 消息监听 **/
    this.messageListenerList = [];
    this.closeEventListenerList = [];



    this.init = function (params , callback){
        if ("WebSocket" in window) {
            var webSocket;
            try {
                //没有url
                if (!this.url) {
                    return false;
                }
                webSocket = new WebSocket(this.url , subprotocols);
                webSocket.onerror = function (event) {
                    console.log(' an error coming... ' , event);
                    if (webSocket.readyState == 3) {
                        //连接已经关闭或者未打开
                        if (callback) {
                            callback(false , '连接失败');
                        }
                    }
                }
            } catch (error) {
                console.log(' error : '+error);
            }


            this.socket = webSocket;
            //this 赋值，为了onopen中对send 等方法初始化
            var appWebSocketObj = this;


            // 打开Socket
            webSocket.onopen = function(event) {
                console.log('webSocket.extensions : ' + webSocket.extensions);
                console.log('webSocket.protocol  : ' + webSocket.protocol);

                //WebSocket.OPEN 数值 1
                if (webSocket.readyState == WebSocket.OPEN) {
                    appWebSocketObj.isOpen = true;

                    webSocket.onmessage = function(message) {
                        // console.log(message.data);
                        appWebSocketObj.onMessage(message);
                    };

                    // 监听Socket的关闭
                    webSocket.onclose = function(event) {
                        appWebSocketObj.onClose(event);
                    };

                    //代理发送
                    appWebSocketObj.send = function (msg){
                        // console.log(' send message ....');
                        try {
                            if (webSocket.readyState == OPEN) {
                                webSocket.send(msg);
                            } else if (webSocket.readyState == CLOSING || webSocket.readyState == CLOSED) {
                                appWebSocketObj.onClose(webSocket);

                                //断线重试
                                if (appWebSocketObj.retrying) {
                                    return ;
                                }
                                appWebSocketObj.retrying = true;
                                appWebSocketObj.doRetry();
                            }
                        } catch (e) {
                            console.err(e);
                            appWebSocketObj.onError(e);
                        }
                    }

                    appWebSocketObj.close = function (){
                        // console.log(' close message ....');
                        appWebSocketObj.manulClosed = true;
                        webSocket.close();
                    }


                    if (callback) {
                        // 十秒发送
                        var int = self.setInterval( function () {
                            appWebSocketObj.send("ping")
                        }, 10 * 1000 );
                        callback(true , '连接成功');
                    }
                } else {
                    if (callback) {
                        callback(false , '浏览器版本过低');
                    }
                }
            }
        } else {
            if (callback) {
                callback(false , '浏览器版本过低');
            }
        }

    };

    this.registMessageListener = function (messageListener){
        if (this.messageListenerList && messageListener) {
            this.messageListenerList.push(messageListener);
        }
    };
    this.registCloseEventListener = function (closeEventListener){
        if (this.closeEventListenerList && closeEventListener) {
            this.closeEventListenerList.push(closeEventListener);
        }
    };

    
    
    this.onMessage = function (message) {
        // console.log(' message coming .....');
        if (this.messageListenerList && this.messageListenerList.length > 0 ) {
            for (var i = 0 ; i < this.messageListenerList.length ; i ++) {
                var listener = this.messageListenerList[i];
                listener(message);
            }
        }
    }


    //关闭
    this.close = function(){
    };

    this.doRetry = function () {
        // console.log('...doRetry start ....');
        var appWebSocketObj = this;
        this.retrying = true;
        // debugger
        if (this.retry > 0) {
            this.init(null , function (success, message){
                // console.log('...doRetry end ....');
                if (!success) {
                    //失败重试
                    appWebSocketObj.doRetry();
                } else {
                    appWebSocketObj.retrying = false;
                    appWebSocketObj.retry = 3;
                }
            });
        }
        this.retry -- ;
    };
    

    //关闭
    this.onClose = function(event){
        this.isOpen = false;
        if (this.closeEventListenerList && this.closeEventListenerList.length > 0 ) {
            for (var i = 0 ; i < this.closeEventListenerList.length ; i ++) {
                var listener = this.closeEventListenerList[i];
                listener(event);
            }
        }

        //手动关闭不触发重试
        if (this.manulClosed) {
        } else {
        }
    };

    //关闭
    this.onError = function(data){
        this.isOpen = false;
    };




    //失败重试
    this.onFinalRetryError = function(data){
        this.isOpen = false;
    };

}



