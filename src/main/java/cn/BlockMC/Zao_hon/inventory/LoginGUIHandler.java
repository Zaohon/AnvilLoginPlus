package cn.BlockMC.Zao_hon.inventory;

import org.bukkit.entity.Player;

import cn.BlockMC.Zao_hon.AnvilLogin;
import cn.BlockMC.Zao_hon.PlayerAuthInfo;
import cn.BlockMC.Zao_hon.Events.PlayerLoggedEvent;

public class LoginGUIHandler implements AnvilClickEventHandler {
	private AnvilLogin plugin;

	public LoginGUIHandler(AnvilLogin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onAnvilClick(AnvilClickEvent event) {
		event.setWillClose(false);
		Player player = event.getPlayer();
		PlayerAuthInfo info = plugin.getPlayerAuthInfoManager().getPlayerAuthInfo(player.getUniqueId());
		if (event.getSlot() == AnvilSlot.OUPUT_2) {
			String password = info.getPassword();
			if (password.equals(event.getName())) {
				event.setWillClose(true);
				info.setIP(AnvilLogin.getPresentTime());
				plugin.getPlayerAuthInfoManager().setPlayerAuthInfo(player.getUniqueId(), info);
				plugin.getLoggedPlayer().add(player.getUniqueId());
				PlayerLoggedEvent e = new PlayerLoggedEvent(player);
				plugin.getServer().getPluginManager().callEvent(e);
				plugin.sendWelcomeMessage(player);
				return;
			} else {
				plugin.PR(info.getName() + "尝试使用错误的密码" + event.getName() + "进入服务器");
				player.kickPlayer("密码错误");
				return;
			}
		}
	}

}
