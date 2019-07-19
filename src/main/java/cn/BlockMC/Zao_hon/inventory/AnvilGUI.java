package cn.BlockMC.Zao_hon.inventory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import cn.BlockMC.Zao_hon.AnvilLoginPlus;
import cn.BlockMC.Zao_hon.Util.NMSManager;

public class AnvilGUI {
	private static Class<?> ChatMessage = NMSManager.getNMSClass("ChatMessage");
	private static Class<?> ContainerAnvil = NMSManager.getNMSClass("ContainerAnvil");
	private static Class<?> BlockPosition = NMSManager.getNMSClass("BlockPosition");
	private static Class<?> EntityHuman = NMSManager.getNMSClass("EntityHuman");
	private static Class<?> PacketPlayOutOpenWindow = NMSManager.getNMSClass("PacketPlayOutOpenWindow");

	private Player player;
	private Listener listener;
	private Inventory inventory;
	private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
	private BukkitTask autoOpenTask;
	private AnvilClickEventHandler handler;

	public AnvilGUI(Player player, final AnvilClickEventHandler handler, AnvilLoginPlus plugin) {
		this.player = player;
		this.handler = handler;
		this.listener = new AnvilGUIListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);

		if (plugin.getConfig().getBoolean("AutoOpen")) {
			 autoOpenTask=	Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new AutoOpenRunnable(this), 20, 20);
		}
	}

	public void open() {
		try {
			player.setLevel(player.getLevel() + 1);
			Object p = NMSManager.getHandle(player);

			Object container = ContainerAnvil
					.getConstructor(NMSManager.getNMSClass("PlayerInventory"), NMSManager.getNMSClass("World"),
							BlockPosition, EntityHuman)
					.newInstance(NMSManager.getField("inventory", p), NMSManager.getField("world", p),
							BlockPosition.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(0, 0, 0),
							p);

			NMSManager.setField("checkReachable", container, false);

			Object bukkitview = NMSManager.invokeMethod("getBukkitView", container);
			inventory = (Inventory) NMSManager.invokeMethod("getTopInventory", bukkitview);
			items.forEach((k, v) -> inventory.setItem(k.getSlot(), v));

			int c = (int) NMSManager.invokeMethod("nextContainerCounter", p);

			Object chatmessage = ChatMessage.getConstructor(String.class, Object[].class).newInstance("§e铁砧登录系统",
					new Object[0]);
			Object playerconnection = NMSManager.getField("playerConnection", p);
			Object packet = PacketPlayOutOpenWindow.getConstructor(Integer.TYPE, String.class,
					NMSManager.getNMSClass("IChatBaseComponent"), Integer.TYPE)
					.newInstance(c, "minecraft:anvil", chatmessage, Integer.valueOf(0));

			NMSManager.getMethod("sendPacket", playerconnection.getClass(),
					new Class[] { NMSManager.getNMSClass("Packet") }).invoke(playerconnection, packet);

			Field activeContainerField = NMSManager.getField("activeContainer", p.getClass());
			if (activeContainerField != null) {
				activeContainerField.set(p, container);

				NMSManager.getField("windowId", NMSManager.getNMSClass("Container")).set(activeContainerField.get(p),
						Integer.valueOf(c));

				NMSManager
						.getMethod("addSlotListener", activeContainerField.get(p).getClass(),
								new Class[] { NMSManager.getNMSClass("ICrafting") })
						.invoke(activeContainerField.get(p), new Object[] { p });
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException
				| NoSuchMethodException | SecurityException e) {

		}
	}

	public Player getPlayer() {
		return this.player;
	}

	public ItemStack getItem(AnvilSlot slot) {
		return items.get(slot);
	}

	public void setSlot(AnvilSlot slot, ItemStack item) {
		items.put(slot, item);
	}

	public Inventory getInventory() {
		return inventory;
	}

	public AnvilClickEventHandler getHandler() {
		return handler;
	}

	public void destroy() {
		HandlerList.unregisterAll(listener);
		if (autoOpenTask != null)
			autoOpenTask.cancel();
		inventory.clear();
		player.closeInventory();
	}

	private class AutoOpenRunnable implements Runnable {
		final AnvilGUI gui;

		AutoOpenRunnable(AnvilGUI gui) {
			this.gui = gui;
		}

		@Override
		public void run() {
			try {
				if (gui.getPlayer().getOpenInventory().getType() != InventoryType.ANVIL) {
					gui.open();
				}
			} catch (IllegalArgumentException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
}
