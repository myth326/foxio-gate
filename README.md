# foxio-gate
一个基于netty的tcp网关. 整合springBoot,支持socket/websocket. 分布式集群游戏框架.<br/>


环境要求： jdk1.8+ 注意不能用 jre <br/>


 通讯方式：<br/>
	1 启动网关与子服，无顺序关系<br/>
	2 子服订阅相关消息<br/>
	3 当网关收到消息后，识别主题，将相应消息发送给订阅者<br/>
	4 子服可取消订阅，可订阅多种主题<br/>
	5 子服可以是订阅者，也可以是发布者<br/>
	6 订阅主题分两个标识，main + sub ，以便于精细化的订阅发布<br/>

<br/>
<br/>
<br/>

联系  yuzhonga@qq.com 吉祥<br/>
