package cn.BlockMC.Zao_hon;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AnvilGUI {
	private static Class<?> ChatMessage = NMSManager.getNMSClass("ChatMessage");
	private static Class<?> ContainerAnvil = NMSManager.getNMSClass("ContainerAnvil");
	private static Class<?> BlockPosition = NMSManager.getNMSClass("BlockPosition");
	private static Class<?> EntityHuman = NMSManager.getNMSClass("EntityHuman");
	private static Class<?> PacketPlayOutOpenWindow = NMSManager.getNMSClass("PacketPlayOutOpenWindow");

	// private AnvilLogin plugin;
	private Player player;
	// private AnvilClickEventHandler handler;
	private Listener listener;
	private Inventory inventory;
	private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
	private BukkitRunnable autoopenrun;

	public AnvilGUI(Player p, final AnvilClickEventHandler handler, AnvilLogin plugin) {
		this.player = p;
		// this.handler = handler;

		this.listener = new Listener() {
			@EventHandler
			public void onInventoryClick(InventoryClickEvent e) {
				if (e.getWhoClicked() instanceof Player) {
					// Player p = (Player) e.getWhoClicked();
					if (e.getInventory().equals(inventory)) {
						e.setCancelled(true);
						ItemStack item = e.getCurrentItem();
						if (item == null || !item.hasItemMeta()) {
							return;
						}
						String name = item.getItemMeta().getDisplayName();

						int slot = e.getRawSlot();

						AnvilClickEvent event = new AnvilClickEvent(name, AnvilSlot.getBySlot(slot));
						handler.onAnvilClick(event);

						if (event.getWillClose()) {
							destroy();
							BookUtil.openBook(p);
							p.setLevel(p.getLevel() - 1);
						}
					}
				}
			}

			@EventHandler
			public void onInventoryClose(InventoryCloseEvent e) {
				if (e.getInventory().equals(inventory)) {
					inventory.clear();
					Player p = (Player) e.getPlayer();
					p.setLevel(p.getLevel() - 1);
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent e) {
				if (e.getPlayer().equals(player)) {
					if (inventory != null) {
						inventory.clear();
						destroy();
						if (p.getOpenInventory().getType() == InventoryType.ANVIL) {
							p.setLevel(p.getLevel() - 1);
						}
					}
				}
			}

		};
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);

		if (plugin.getConfig().getBoolean("AutoOpen")) {
			autoopenrun = new BukkitRunnable() {

				@Override
				public void run() {
					try {
						if (p.getOpenInventory().getType() != InventoryType.ANVIL) {
							open();
						}
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}

			};
			autoopenrun.runTaskTimer(plugin, 40l, 40l);
		}
	}

	public void open() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		player.setLevel(player.getLevel() + 1);
		Object p = NMSManager.getHandle(player);

		Object container = ContainerAnvil
				.getConstructor(NMSManager.getNMSClass("PlayerInventory"), NMSManager.getNMSClass("World"),
						BlockPosition, EntityHuman)
				.newInstance(NMSManager.getField("inventory", p), NMSManager.getField("world", p),
						BlockPosition.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(0, 0, 0), p);

		NMSManager.setField("checkReachable", container, false);

		Object bukkitview = NMSManager.invokeMethod("getBukkitView", container);
		inventory = (Inventory) NMSManager.invokeMethod("getTopInventory", bukkitview);
		items.forEach((k, v) -> inventory.setItem(k.getSlot(), v));

		int c = (int) NMSManager.invokeMethod("nextContainerCounter", p);

		Object chatmessage = ChatMessage.getConstructor(String.class, Object[].class).newInstance("fuck you",
				new Object[0]);
		Object playerconnection = NMSManager.getField("playerConnection", p);
		Object packet = PacketPlayOutOpenWindow
				.getConstructor(Integer.TYPE, String.class, NMSManager.getNMSClass("IChatBaseComponent"), Integer.TYPE)
				.newInstance(c, "minecraft:anvil", chatmessage, Integer.valueOf(0));

		NMSManager
				.getMethod("sendPacket", playerconnection.getClass(), new Class[] { NMSManager.getNMSClass("Packet") })
				.invoke(playerconnection, packet);

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

	private void destroy() {
		HandlerList.unregisterAll(listener);
		if (autoopenrun != null)
			autoopenrun.cancel();
		inventory.clear();
		player.closeInventory();
	}

	public static enum AnvilSlot {

		INPUT_0(0), INPUT_1(1), OUPUT_2(2);

		private int slot;

		AnvilSlot(int slot) {
			this.slot = slot;
		}

		public int getSlot() {
			return this.slot;
		}

		public static AnvilSlot getBySlot(int slot) {
			AnvilSlot[] slots = AnvilSlot.values();
			int j = slots.length;
			for (int i = 0; i < j; i++) {
				AnvilSlot anvilslot = slots[i];

				if (anvilslot.getSlot() == slot)
					return anvilslot;
			}
			return null;
		}

	}

	public static class AnvilClickEvent {
		private final AnvilSlot slot;
		private final String name;
		private boolean close;

		public AnvilClickEvent(final String name, final AnvilSlot slot) {
			this.slot = slot;
			this.name = name;
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

		public void setWillClose(boolean close) {
			this.close = close;
		}
	}

	public static abstract interface AnvilClickEventHandler {
		public abstract void onAnvilClick(AnvilClickEvent event);
	}

}
