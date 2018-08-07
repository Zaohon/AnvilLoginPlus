package cn.BlockMC.Zao_hon.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLoggedEvent extends Event{
	private static final HandlerList HANDLERS = new HandlerList();
	private final Player player;
	public PlayerLoggedEvent(final Player player){
		this.player = player;
	}
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
	public Player getPlayer(){
		return player;
	}

}
