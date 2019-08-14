package cn.foxio.simple;

import org.apache.log4j.Logger;

import cn.foxio.gate.face.AbstractAccepter;
import cn.foxio.gate.face.IMessageBox;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author lucky
 *
 */
public class SimpleMqAccepter extends AbstractAccepter<IMessageBox> {
	
	
	private Logger logger = Logger.getLogger(SimpleMqAccepter.class);
	
	@Override
	protected  Logger getLogger(){
		return logger;
	}


	public SimpleMqAccepter() {
		this.start();

	}

	@Override
	protected boolean handlerMsg(IMessageBox msg) {

		System.out.println("GateWary SimpleMQAccepter : " + msg.toJson());
		int cmdId = msg.getType();

		switch (cmdId) {
		case 1:

			return true;

		default:
			break;
		}

		return false;
	}

	@Override
	public void acceptMsg(IMessageBox msg, ChannelHandlerContext ctx) {
		super.acceptMsg(msg, ctx);
	}

	@Override
	public String getServerName() {
		return "simpleMQ";
	}

}