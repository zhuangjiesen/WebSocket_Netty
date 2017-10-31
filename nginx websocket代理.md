# nginx websocket代理


官网配置 http://nginx.org/en/docs/http/websocket.html
官网配置有个问题：
针对于 uri 进行，现实中可能会出现 8080 端口(http/tomcat) 38888端口(ws协议/netty) 监听
而用nginx 进行反向代理只开放 80 端口
所以在nginx 就需要监听所有 80 端口的请求，判断请求头然后进行动态判断websocket进行跳转

1.一种现在讲的配置 if 判断然后进行反向代理
2. 编译Lua 模块进行编码，环境配置难度大


nginx 版本号 1.12.1 
nginx.conf中

```
	
	....

	# 定义变量
    map $http_upgrade $connection_upgrade {
        default upgrade;
        ''      close;
    }


    server {
        listen       8080;
        server_name  localhost;
		...

	   	location / {
            root   html;
            
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection $connection_upgrade;
            # 判断 websocket请求
    	    if ( $http_upgrade = "websocket" ) {

                # rewrite  (.*)  http://10.11.165.101:38888$1;
                proxy_pass http://10.11.165.101:38888;
    		}
            
            index  index.html index.htm;

        }

	
		...
    }


```