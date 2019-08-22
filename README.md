# foxio-gate
一个基于netty的tcp网关. 整合springBoot,支持socket/websocket. 分布式集群游戏框架.

 通讯方式
	1 启动网关与子服，无顺序关系<br/>
	2 子服订阅相关消息<br/>
	3 当网关收到消息后，识别主题，将相应消息发送给订阅者
	4 子服可取消订阅，可订阅多种主题
	5 子服可以是订阅者，也可以是发布者
	6 订阅主题分两个标识，main + sub ，以便于精细化的订阅发布



联系  yuzhonga@qq.com 吉祥
