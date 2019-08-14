package cn.foxio.gate.tcp.gateway;

import cn.foxio.gate.tcp.data.OriginalPackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码
 * @author Lucky
 *
 */
public class FoxNettyEncoder extends MessageToByteEncoder<OriginalPackage>{

		
	@Override
	protected void encode(ChannelHandlerContext ctx, OriginalPackage msg, ByteBuf buf) throws Exception {
		buf.writeBytes( msg.toByteArrays() );
	}

}
