package cn.foxio.simple.cmd;


import com.alibaba.fastjson.annotation.JSONField;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import cn.foxio.gate.face.IMsg;
import cn.foxio.gate.tools.GsonUtil;
import cn.foxio.gate.tools.ProtoBufUtils;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * SimplePlayer
 * version:0.1
 * @author message create tools
 * @Proto(type = -1,code=-1,clazz="",desc=" 玩家简单信息")  
 */
@Data
@Accessors(chain = true)
public class SimplePlayer implements IMsg{

		
	
	
	//----------外层public Msg类 start ---------------------------------------------------------------
	
  	/** 玩家id **/
  	@Protobuf(order = 1 , fieldType =FieldType.INT64 ,description="玩家id")
    protected long userId;
  	/** 玩家名字 **/
  	@Protobuf(order = 2  ,description="玩家名字")
    protected String nickName="";
  	/** 头相 **/
  	@Protobuf(order = 3  , fieldType = FieldType.INT32 ,description="头相")
    protected int headPhotoId;
  	/** 金钱 **/
  	@Protobuf(order = 4 , fieldType =FieldType.INT64 ,description="金钱")
    protected long gold;
  	/** 是否在线 **/
  	@Protobuf(order = 5  ,description="是否在线")
    protected boolean online;
  	/** 状态 **/
  	@Protobuf(order = 6  , fieldType = FieldType.INT32 ,description="状态")
    protected int status;
  	/** 坐位号 **/
  	@Protobuf(order = 7  , fieldType = FieldType.INT32 ,description="坐位号")
    protected int posId;
  
  public SimplePlayer(){ }
	//----------外层public Msg类  end ---------------------------------------------------------------
	//----------通用接口方法 start ---------------------------------------------------------------

	//static public final int msgId = -1;
	
	@Override
	public int getCmdId(){ return -1; }

		
	@JSONField(serialize = false)
	@Override
	public String getCmdClassName(){
		return "com.xts.game.app.pubmsg.command.CmdSimplePlayer";
	}
	
	@Override
	public int getModuleId(){
		return -1;
	}

	
	@Override
	public byte[] toByteArrays() {		
		return ProtoBufUtils.encode(this);
	}
	
	@Override
	@JSONField(serialize = false)
	public String getDesc() {
		return " 玩家简单信息";
	}
	
	public static SimplePlayer parseFromJson(String str) {
		SimplePlayer msg = null;
		try {
			msg = GsonUtil.fromJson(str,SimplePlayer.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	//----------通用接口方法end ---------------------------------------------------------------
	

	
	
	
}