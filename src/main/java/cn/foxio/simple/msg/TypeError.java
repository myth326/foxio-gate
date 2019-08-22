package cn.foxio.simple.msg;

import com.alibaba.fastjson.annotation.JSONField;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import cn.foxio.gate.face.IMsg;
import cn.foxio.gate.tools.ProtoBufUtils;
import lombok.Data;

/**
 * 错误数据
 * 
 * @author lucky
 *
 */
public class TypeError {
	@Data
	static public class SystemError implements IMsg {

		// ----------外层public Msg类 start
		// ---------------------------------------------------------------

		/** 错误id **/
		@Protobuf(order = 1, fieldType = FieldType.INT32, description = "错误id")
		protected int errId;
		/** 错误信息 **/
		@Protobuf(order = 2, description = "错误信息")
		protected String errMsg = "";

		public SystemError() {
		}

		public SystemError(int errId, String errMsg) {
			super();
			this.errId = errId;
			this.errMsg = errMsg;
		}

		/**
		 * 转系统错误信息
		 * 
		 * @return
		 */
		public SystemError toErrorMsg() {
			SystemError msg = new SystemError();
			msg.setErrId(this.errId);
			msg.setErrMsg(this.errMsg);
			return msg;
		}

		/**
		 * 转系统错误信息
		 * 
		 * @return
		 */
		public SystemError toErrorMsg(Object... args) {
			SystemError msg = new SystemError();
			msg.setErrId(this.errId);
			msg.setErrMsg(String.format(errMsg, args));
			return msg;
		}

		@Override
		public int getCmdId() {
			return 101;
		}

		@Override
		public int getModuleId() {
			return 1;
		}

		@Override
		public byte[] toByteArrays() {
			return ProtoBufUtils.encode(this);
		}

		@Override
		@JSONField(serialize = false)
		public String getDesc() {
			return "      Server-> Client  错误信息";
		}

		@Override
		public String getCmdClassName() {
			return "--";
		}

	}

	

	/**********************************
	 * 帐号模块
	 ***************************/
	static public final SystemError TOKEN_ERROR = new SystemError(300, "亲，登录异常了，重新试试吧!");

	static public final SystemError ERROR_ACCOUNT = new SystemError(301, "亲，您的账号异常：[%s]，去联系客服咨询下吧~");

	static public final SystemError REPEAT_LOGIN = new SystemError(302, "亲，你的账号在另一个地方登录了，当前已被挤下线了~");

	static public final SystemError NOT_LOGIN = new SystemError(303, "亲，你还没有登录或已断线");
	
	
	


}
