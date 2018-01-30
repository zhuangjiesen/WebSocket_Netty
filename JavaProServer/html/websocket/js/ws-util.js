/*
* wsmessage
* {
*   header :
*   topic :
*   contentType:
*   content :
* }
*
*
* */
var WsClient = function (url , protocols ) {

    this.attachment = null;
    this.topics = null;
    this.hasTopic = false;
    this.url = url;
    this.topics = null;
    this.socket =  null;

    var _this = this;

    this.onerror = null;
    WsClient.prototype.registError = function (callback ) {
        this.onerror = callback;
    }


    //默认消息监听
    this.defaultListener = function (message) {
    };

    this.onMessage = function (message) {
        var wsmessage = JSON.parse(message.data);
        var mtopic = wsmessage.topic;
        var handler = getTopicHandler(mtopic);
        if (handler) {
            handler(wsmessage.content);
        } else {
            this.defaultListener(wsmessage);
        }
    }




    WsClient.prototype.connect = function (options , callback ) {
        if ("WebSocket" in window) {
            var webSocket;
            try {
                //没有url
                if (!this.url) {
                    return false;
                }
                var params = "";
                if (this.topics) {
                    params += 'topics='
                    var topicsStr = '';
                    for (key in topics) {
                        topicsStr += key;
                        topicsStr += ',';
                    }
                    if (topicsStr.length > 0) {
                        topicsStr = topicsStr.substring(0 , topicsStr.length - 1);
                    }
                    params += topicsStr;
                }
                if (params.length > 0) {
                    this.url = this.url + '?' + params;
                }
                webSocket = new WebSocket(this.url , protocols );
                webSocket.onerror = function (event) {
                    console.log(' an error coming... ' , event);
                    if (webSocket.readyState == 3) {
                        //连接已经关闭或者未打开
                        if (this.onerror) {
                            this.onerror(event);
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
                //WebSocket.OPEN 数值 1
                if (webSocket.readyState == WebSocket.OPEN) {
                    webSocket.onmessage = function(message) {
                        // console.log(message.data);
                        appWebSocketObj.onMessage(message);
                    };

                    // 监听Socket的关闭
                    webSocket.onclose = function(event) {
                        appWebSocketObj.onClose(event);
                    };

                    appWebSocketObj.close = function (){
                        webSocket.close();
                    }

                }

                if (callback) {
                    callback(appWebSocketObj);
                }
            }
        } else {
            if (this.onerror) {
                this.onerror(event , '浏览器版本过低');
            }
        }

    };

    WsClient.prototype.subscribe = function (topic , topicHandler ) {
        if (!this.topics) {
            this.topics = {};
        }
        this.topics[topic] = topicHandler;
        this.hasTopic = true;

        var sock = this.socket;
        if (sock.readyState == WebSocket.OPEN) {
            var wsMsg = {};
            wsMsg.topic = topic;
            wsMsg.contentType = 'subscribe';
            wsMsg.content = null;
            sock.send(JSON.stringify(wsMsg));
        } else {
            if (this.onerror) {
                this.onerror(event , '连接已关闭');
            }
        }
    };

    WsClient.prototype.unsubscribe = function (topic , topicHandler ) {
        deleteTopic(topic);

        var sock = this.socket;
        if (sock.readyState == WebSocket.OPEN) {
            var wsMsg = {};
            wsMsg.topic = topic;
            wsMsg.contentType = 'unsubscribe';
            wsMsg.content = null;
            sock.send(JSON.stringify(wsMsg));
        } else {
            if (this.onerror) {
                this.onerror(event , '连接已关闭');
            }
        }
    };

    WsClient.prototype.send = function (message ) {
        this.sendByTopic(null ,message );
    };

    WsClient.prototype.sendByTopic = function ( topic , message ) {
        var sock = this.socket;
        if (sock.readyState == WebSocket.OPEN) {
            var wsMsg = {};
            wsMsg.topic = topic;
            wsMsg.content = message;
            sock.send(JSON.stringify(wsMsg));
        } else {
            if (this.onerror) {
                this.onerror(event , '连接已关闭');
            }
        }
    };


    function deleteTopic( topic ){
        delete _this.topics[topic];
        var topics = _this.topics;
        if ((!topics) || topics.length == 0) {
            _this.hasTopic = false;
        }
    }


    //获取主题处理器
    function handleTopic( topic , greeting ){
        if (_this.hasTopic) {
            var handler = _this.topics[topic];
            if (handler) {
                handler(greeting);
            }
        }
    }

    //获取主题处理器
    function getTopicHandler(topic){
        if (_this.hasTopic) {
            return _this.topics[topic];
        }
        return null;
    }


    function isConnAlive () {
    }



}





