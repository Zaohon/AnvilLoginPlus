package cn.BlockMC.Zao_hon.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import cn.BlockMC.Zao_hon.Util.BookUtil;

public class AnvilGUIListener implements Listener {
	private final AnvilGUI gui;

	public AnvilGUIListener(AnvilGUI gui) {
		this.gui = gui;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if (e.getInventory().equals(gui.getInventory())) {
				e.setCancelled(true);
				ItemStack item = e.getCurrentItem();
				if (item == null || !item.hasItemMeta()) {
					return;
				}
				String name = item.getItemMeta().getDisplayName();

				int slot = e.getRawSlot();

				AnvilClickEvent event = new AnvilClickEvent(name, AnvilSlot.getBySlot(slot), p);
				gui.getHandler().onAnvilClick(event);

				if (event.getWillClose()) {
					gui.destroy();
					BookUtil.openBook(p);
					p.setLevel(p.getLevel() - 1);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().equals(gui.getInventory())) {
			gui.getInventory().clear();
			Player p = (Player) e.getPlayer();
			p.setLevel(p.getLevel() - 1);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (e.getPlayer().equals(gui.getPlayer())) {
			if (gui.getInventory() != null) {
				gui.getInventory().clear();
				gui.destroy();
				Player player = e.getPlayer();
				if (player.getOpenInventory().getType() == InventoryType.ANVIL) {
					player.setLevel(player.getLevel() - 1);
				}
			}
		}
	}

};
