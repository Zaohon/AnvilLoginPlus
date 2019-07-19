package cn.BlockMC.Zao_hon;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cn.BlockMC.Zao_hon.inventory.AnvilClickEventHandler;
import cn.BlockMC.Zao_hon.inventory.AnvilGUI;
import cn.BlockMC.Zao_hon.inventory.AnvilSlot;
import cn.BlockMC.Zao_hon.inventory.LoginGUIHandler;
import cn.BlockMC.Zao_hon.inventory.RegisterGUIHandler;

public class EventListener implements Listener {
	// private HashSet<UUID> logged = new HashSet<UUID>();
	// private HashMap<UUID, AnvilGUI> unlogged = new HashMap<UUID, AnvilGUI>();
	private AnvilLogin plugin;
	// private Mysql sql;

	public EventListener(AnvilLogin plugin) {
		this.plugin = plugin;
		// sql = plugin.getSqlManager();
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {

		// final Player p = e.getPlayer();
		// final UUID u = p.getUniqueId();
		//
		// final String password = sql.selectPlayerPassword(p);
		//
		// boolean registed = sql.isRegistered(p.getUniqueId());
		Player player = e.getPlayer();
		PlayerAuthInfo info = plugin.getPlayerAuthInfoManager().getPlayerAuthInfo(player.getUniqueId());
		boolean registed = info != null;
		AnvilClickEventHandler handler = registed ? new LoginGUIHandler(plugin) :new RegisterGUIHandler(plugin) ;
		AnvilGUI gui = new AnvilGUI(player, handler, plugin);
		ItemStack paper = new ItemStack(Material.PAPER);
		ItemMeta papermeta = paper.getItemMeta();
		if (registed) {
			papermeta.setDisplayName("请输入登录密码");
			papermeta.setLore(Arrays.asList("§a输入之前注册过的密码来登录"));
		} else {
			papermeta.setDisplayName("请输入你的注册密码");
			papermeta.setLore(Arrays.asList("§a这是你第一次进入服务器", "§a这里的§d注册密码§a将在以后使用", "§c请务必牢记"));
		}
		paper.setItemMeta(papermeta);
		gui.setSlot(AnvilSlot.INPUT_0, paper);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> gui.open());
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if (player.isOnline() && !plugin.getLoggedPlayer().contains(player.getUniqueId())) {
				player.kickPlayer("登录超时");
			}
		} , plugin.getConfig().getInt("TimeToKick") * 20);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		UUID u = e.getPlayer().getUniqueId();
		plugin.getLoggedPlayer().remove(u);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		UUID u = e.getPlayer().getUniqueId();
		if (!plugin.getLoggedPlayer().contains(u)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		UUID u = e.getPlayer().getUniqueId();
		if (!plugin.getLoggedPlayer().contains(u))
			e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		UUID u = e.getPlayer().getUniqueId();
		if (!plugin.getLoggedPlayer().contains(u)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteractInv(InventoryInteractEvent e) {
		UUID u = e.getWhoClicked().getUniqueId();
		if (!plugin.getLoggedPlayer().contains(u)) {
			e.setCancelled(true);
		}
	}

}
