package cn.BlockMC.Zao_hon;

import java.util.HashMap;
import java.util.UUID;

public class PlayerAuthInfoManager {
	private AnvilLogin plugin;
	private HashMap<UUID, PlayerAuthInfo> playerAuthInfo = new HashMap<UUID, PlayerAuthInfo>();

	public PlayerAuthInfoManager(AnvilLogin plugin) {
		this.plugin = plugin;
	}

	public PlayerAuthInfo getPlayerAuthInfo(UUID uuid) {
		if (playerAuthInfo.containsKey(uuid)) {
			return playerAuthInfo.get(uuid);
		} else {
			PlayerAuthInfo authInfo = plugin.getDataStorager().getPlayerAuthInfo(uuid);
			if (authInfo != null) {
				playerAuthInfo.put(uuid, authInfo);
			}
			return authInfo;
		}
	}

	public void setPlayerAuthInfo(UUID uuid, PlayerAuthInfo info) {
		playerAuthInfo.put(uuid, info);
		plugin.getDataStorager().setPlayerInfo(uuid, info);
	}
}
