package cn.foxio.simple.cmd;

import cn.foxio.gate.face.IMessageBox;
import cn.foxio.gate.face.IPlayerCommand;
import cn.foxio.gate.helper.SenderHelper;
import cn.foxio.gate.tcp.SubService;
import cn.foxio.simple.config.ServerName;
import cn.foxio.simple.msg.LoginCtsMsg;

/**
 * 处理登录消息
 * 
 * @author lucky
 *
 */
public class CmdLoginCts implements IPlayerCommand<IMessageBox, LoginCtsMsg> {

	/**
	 * 登陆服务
	 */
	private SubService app = SenderHelper.get(ServerName.LOGIN_KEY);

	@Override
	public Object execute(IMessageBox box, LoginCtsMsg msg, Object... args) {


		// msg.getToken();

		LoginStcMsg stc = new LoginStcMsg();
		stc.setToken(msg.getToken());
		stc.setGold(11111);

		long userId = 3000;

		SimplePlayer player = new SimplePlayer();
		player.setHeadPhotoId(1);
		player.setGold(5000);
		player.setNickName("张三");
		player.setOnline(true);
		player.setPosId(-1);
		player.setStatus(1);
		player.setUserId(userId);

		stc.setPlayerInfo(player);
		// 成功登陆 回给客户端
		app.send(userId, stc);

		return null;
	}

	// private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd
	// HH:mm:ss");

	@Override
	public boolean binding(IMessageBox box, LoginCtsMsg msg, Object... args) {
		return true;
	}

	@Override
	public boolean check(IMessageBox box, LoginCtsMsg msg, Object... args) {
		return true;
	}

}
