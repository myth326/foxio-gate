package cn.foxio.gate.tcp.data;

import org.springframework.stereotype.Component;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import cn.foxio.gate.face.IInnerMessage;
import cn.foxio.gate.tools.FoxProtobufUtils;
import lombok.Data;

/**
 * 心跳
 * @author lucky
 *
 */
@Data
@Component
public class HeartbeatData implements IInnerMessage{

	/**
	 * 当前时间
	 */
	@Protobuf(order = 1 )
	protected long currTime = System.currentTimeMillis();

	
	public HeartbeatData(){
		
	}
	
	@Override
	public int getCmdId() {
		return 0;
	}
	
	@Override
	public byte[] toByteArrays() {
		return FoxProtobufUtils.encode(this);
	}
}
