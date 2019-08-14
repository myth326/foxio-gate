package cn.foxio.gate.tcp.gateway;

import java.util.List;

import cn.foxio.gate.tcp.data.OriginalPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

/**
 * 解码
 * @author Lucky
 *
 */
public class FoxNettyDecoder extends ByteToMessageDecoder{
	
	
	
	static private AttributeKey<OriginalPackage> PACK_KEY = AttributeKey.valueOf("Fox_Socket_Pack");
	
	public FoxNettyDecoder() {
		
	}
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf bufferIn, List<Object> out) throws Exception {
		
		
		//ctx.attr(key)
		
		OriginalPackage pack = ctx.channel().attr(PACK_KEY).get();
		
		if (bufferIn.readableBytes() > 0 ) {
			try {
		
				//没有读包头
				if ( pack == null || ! pack.isReadHead() ) {
					
					if (bufferIn.readableBytes() >= OriginalPackage.HEAD_SIZE ) {
					
						if ( pack == null ) {
							pack = new OriginalPackage();
						}
						readHead(bufferIn, pack);
					}else {
						//不够包头长度，停止读取
						return;
					}
				}
				
				//已经读包头
				if (bufferIn.readableBytes() >= pack.getBodySize()) {
					readBody(bufferIn, pack);
					out.add(pack);
					ctx.channel().attr(PACK_KEY).set(null);
				}else {
					//字节不够，放入channel;
					ctx.channel().attr(PACK_KEY).set(pack);
					//System.err.println("-- 一包分多次到 !");
				}
				
				
			} catch (Exception e) {
				// 协议解码异常
				ctx.close();
				// 捕获异常
				// 继续抛出异常让netty进行处理
				throw e;
			}
		}	
 
    }
	
	/**
	 * 读包头
	 * @param byteBuf
	 * @param pack
	 * @throws Exception
	 */
	private void readHead(ByteBuf byteBuf , OriginalPackage pack ) throws Exception {
		//byteBuf.order(ByteOrder.LITTLE_ENDIAN);
		pack.setCmdId( ByteBufUtil.swapShort( byteBuf.readShort() ) ); 
		pack.setBodySize( ByteBufUtil.swapInt( byteBuf.readInt() ));
		pack.setKey(ByteBufUtil.swapShort(byteBuf.readShort())); 
		pack.setReadHead(true);
	}
	
	
	/**
	 * 读包体
	 * @param byteBuf
	 * @param pack
	 * @throws Exception
	 */
	private void readBody(ByteBuf byteBuf , OriginalPackage pack ) throws Exception {
		// 数据体完毕即可读协议数据
		ByteBuf buf = byteBuf.readBytes(pack.getBodySize());
		int i = 0;
		byte[] ba = new byte[pack.getBodySize()];
		while(i < pack.getBodySize()) {
			ba[i] = buf.getByte(i);
			i++;
		}
		pack.setProtoData(ba);
		buf.release();
	}
	


}
