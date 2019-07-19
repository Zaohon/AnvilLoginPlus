package cn.BlockMC.Zao_hon.inventory;

import org.bukkit.entity.Player;

public class AnvilClickEvent {
	private final Player player;
	private final AnvilSlot slot;
	private final String name;
	private boolean close;

	public AnvilClickEvent(final String name, final AnvilSlot slot,final Player player) {
		this.slot = slot;
		this.name = name;
		this.player = player;
		this.close = true;
	}

	public String getName() {
		return this.name;
	}

	public AnvilSlot getSlot() {
		return this.slot;
	}

	public boolean getWillClose() {
		return this.close;
	}
	public Player getPlayer(){
		return player;
	}

	public void setWillClose(boolean close) {
		this.close = close;
	}
}
