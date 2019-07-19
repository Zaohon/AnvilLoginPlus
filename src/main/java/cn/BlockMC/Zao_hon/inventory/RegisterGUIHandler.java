package cn.BlockMC.Zao_hon.inventory;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import cn.BlockMC.Zao_hon.AnvilLogin;
import cn.BlockMC.Zao_hon.PlayerAuthInfo;
import cn.BlockMC.Zao_hon.Events.PlayerLoggedEvent;

public class RegisterGUIHandler implements AnvilClickEventHandler {
	private AnvilLogin plugin;

	public RegisterGUIHandler(AnvilLogin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onAnvilClick(AnvilClickEvent event) {
		Player p = event.getPlayer();
		event.setWillClose(false);
		if (event.getSlot() == AnvilSlot.OUPUT_2) {
			if (isContainChinese(p.getName())) {
				p.kickPlayer("§c服务器不支持中文名称！");
				return;
			}
			if (p.getName().length() > 30) {
				p.kickPlayer("§c你的名字太长了");
				return;
			}

			String password = event.getName();
			if (password.equals("")) {
				p.kickPlayer("密码不能为空");
				return;
			}
			int lenth = password.length();
			if (lenth <= 3 || lenth >= 15) {
				p.kickPlayer("§c你的密码长度太长或太短了");
				return;
			}
			if (isContainChinese(password)) {
				p.kickPlayer("§c密码不能包含中文");
				return;
			}
			String ip = p.getAddress().getAddress().getHostName();
			int i = plugin.getDataStorager().getIPAcoount(ip);
			if (i > plugin.getConfig().getInt("IPMaxAccount")) {
				p.kickPlayer("你的ip已经达到账号限制");
				return;
			}
			event.setWillClose(true);
			String presentTime = AnvilLogin.getPresentTime();
			UUID uuid = p.getUniqueId();
			PlayerAuthInfo info = new PlayerAuthInfo(p.getName(), uuid.toString(), password, presentTime, presentTime,
					ip);
			plugin.getPlayerAuthInfoManager().setPlayerAuthInfo(uuid, info);
			plugin.getLoggedPlayer().add(uuid);
			p.sendMessage("§a注册成功！你的密码是:§d" + password + "§a,请牢记");
			PlayerLoggedEvent e = new PlayerLoggedEvent(p);
			plugin.getServer().getPluginManager().callEvent(e);
			plugin.sendWelcomeMessage(p);
			return;
		}
	}

	private boolean isContainChinese(String str) {

		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

}
