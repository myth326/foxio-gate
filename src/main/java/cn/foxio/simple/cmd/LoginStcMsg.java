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
 * LoginStcMsg
 * version:0.1
 * @author message create tools
 * @Proto(type = 3,code=302,clazz="",desc=" 登陆应答")  
 */
@Data
@Accessors(chain = true)
public class LoginStcMsg implements IMsg{

		
	
	
	//----------外层public Msg类 start ---------------------------------------------------------------
	
  	/**  该字段提供，表明应该替换原来的token，防止其他设备登陆成功� **/
  	@Protobuf(order = 1  ,description=" 该字段提供，表明应该替换原来的token，防止其他设备登陆成功�")
    protected String token="";
  	/** 钱 **/
  	@Protobuf(order = 2 , fieldType =FieldType.INT64 ,description="钱")
    protected long gold;
  	/** 玩家(社交)信息 **/
  	@Protobuf(order = 3  ,description="玩家(社交)信息")
    protected SimplePlayer playerInfo;
  
  public LoginStcMsg(){ }
	//----------外层public Msg类  end ---------------------------------------------------------------
	//----------通用接口方法 start ---------------------------------------------------------------

	//static public final int msgId = 302;
	
	@Override
	public int getCmdId(){ return 302; }

		
	@JSONField(serialize = false)
	@Override
	public String getCmdClassName(){
		return "";
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
		return " 登陆应答";
	}
	
	public static LoginStcMsg parseFromJson(String str) {
		LoginStcMsg msg = null;
		try {
			msg = GsonUtil.fromJson(str,LoginStcMsg.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	//----------通用接口方法end ---------------------------------------------------------------
	

	
	
	
}