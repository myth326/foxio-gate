package cn.foxio.simple.msg;


import com.alibaba.fastjson.annotation.JSONField;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import cn.foxio.gate.face.IMsg;
import cn.foxio.gate.tools.GsonUtil;
import cn.foxio.gate.tools.ProtoBufUtils;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * LoginCtsMsg
 * version:0.1
 * @author message create tools
 * @Proto(type = 3,code=301,clazz="",desc=" 登陆")  
 */
@Data
@Accessors(chain = true)
public class LoginCtsMsg implements IMsg{

		
	
	
	//----------外层public Msg类 start ---------------------------------------------------------------
	
  	/** token **/
  	@Protobuf(order = 1  ,description="token")
    protected String token="";
  	/** 1=Android; 2=IOS; 3=Web **/
  	@Protobuf(order = 2  , fieldType = FieldType.INT32 ,description="1=Android; 2=IOS; 3=Web")
    protected int deviceId;
  
  public LoginCtsMsg(){ }
	//----------外层public Msg类  end ---------------------------------------------------------------
	//----------通用接口方法 start ---------------------------------------------------------------

	//static public final int msgId = 301;
	
	@Override
	public int getCmdId(){ return 301; }

		
	@JSONField(serialize = false)
	@Override
	public String getCmdClassName(){
		return "com.xts.game.app.account.command.CmdLoginCts";
	}
	
	@Override
	public int getModuleId(){
		return 3;
	}

	
	@Override
	public byte[] toByteArrays() {		
		return ProtoBufUtils.encode(this);
	}
	
	@Override
	@JSONField(serialize = false)
	public String getDesc() {
		return " 登陆";
	}
	
	public static LoginCtsMsg parseFromJson(String str) {
		LoginCtsMsg msg = null;
		try {
			msg = GsonUtil.fromJson(str,LoginCtsMsg.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	//----------通用接口方法end ---------------------------------------------------------------
	

	
	
	
}