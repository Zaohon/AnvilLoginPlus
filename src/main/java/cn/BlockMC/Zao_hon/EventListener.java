package cn.BlockMC.Zao_hon;

import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import cn.BlockMC.Zao_hon.AnvilGUI.AnvilClickEvent;
import cn.BlockMC.Zao_hon.AnvilGUI.AnvilClickEventHandler;
import cn.BlockMC.Zao_hon.AnvilGUI.AnvilSlot;
import cn.BlockMC.Zao_hon.Events.PlayerLoggedEvent;

public class EventListener implements Listener {
	// private HashSet<UUID> logged = new HashSet<UUID>();
	// private HashMap<UUID, AnvilGUI> unlogged = new HashMap<UUID, AnvilGUI>();
	private AnvilLogin plugin;
	private Mysql sql;

	public EventListener(AnvilLogin plugin) {
		this.plugin = plugin;
		sql = plugin.getSqlManager();
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {

		final Player p = e.getPlayer();
		final UUID u = p.getUniqueId();

		final String password = sql.selectPlayerPassword(p);

		boolean registed = sql.isRegistered(p.getUniqueId());

		if (registed) {
			AnvilGUI gui = new AnvilGUI(p, new AnvilClickEventHandler() {
				@Override
				public void onAnvilClick(AnvilClickEvent event) {
					event.setWillClose(false);
					if (event.getSlot() == AnvilSlot.OUPUT_2) {
						if (password.equals(event.getName())) {
							event.setWillClose(true);
							sql.updatePlayer(p);
							plugin.getLoggedPlayer().add(u);
							PlayerLoggedEvent e = new PlayerLoggedEvent(p);
							plugin.getServer().getPluginManager().callEvent(e);
							sendWelcomeMessage(p);
							return;
						} else {
							plugin.getLogger().info(p.getName() + "尝试使用错误的密码" + event.getName() + "进入服务器");
							p.kickPlayer("密码错误");
							return;
						}
					}
				}
			}, plugin);
			ItemStack paper = new ItemStack(Material.PAPER);
			ItemMeta papermeta = paper.getItemMeta();
			papermeta.setDisplayName("请输入登录密码");
			papermeta.setLore(Arrays.asList("§a输入之前注册过的密码来登录"));
			paper.setItemMeta(papermeta);
			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_0, paper);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				try {
					gui.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});

		} else {
			AnvilGUI gui = new AnvilGUI(p, new AnvilClickEventHandler() {

				@Override
				public void onAnvilClick(AnvilClickEvent event) {
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
							p.sendMessage("密码不能为空");
							return;
						}
						int lenth = password.length();
						if (lenth <= 3 || lenth >= 15) {
							p.sendMessage("§c你的密码长度太长或太短了");
							return;
						}
						if (isContainChinese(password)) {
							p.sendMessage("§c密码不能包含中文");
							return;
						}
						if (sql.getUsersNumber(p.getAddress().getAddress().getHostName()) > plugin.getConfig()
								.getInt("IPMaxAccount")) {
							p.kickPlayer("你的ip已经达到账号限制");
							return;
						}
						event.setWillClose(true);
						sql.playerRegisted(p, password);
						UUID u = p.getUniqueId();
						plugin.getLoggedPlayer().add(u);
						p.sendMessage("§a注册成功！你的密码是:§d" + password + "§a,请牢记");
						PlayerLoggedEvent e = new PlayerLoggedEvent(p);
						plugin.getServer().getPluginManager().callEvent(e);
						sendWelcomeMessage(p);
						return;

					}

				}

			}, plugin);
			ItemStack paper = new ItemStack(Material.PAPER);
			ItemMeta papermeta = paper.getItemMeta();
			papermeta.setDisplayName("请输入你的注册密码");
			papermeta.setLore(Arrays.asList("§a这是你第一次进入服务器", "§a这里的§d注册密码§a将在以后使用", "§c请务必牢记"));
			paper.setItemMeta(papermeta);
			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_0, paper);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				try {
					gui.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if (p.isOnline()&&!plugin.getLoggedPlayer().contains(p.getUniqueId())) {
				p.kickPlayer("登录超时");
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

	private boolean isContainChinese(String str) {

		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	private void sendWelcomeMessage(Player p) {
		plugin.getConfig().getStringList("WelcomeMessage").forEach(m -> p
				.sendMessage(ChatColor.translateAlternateColorCodes('&', m.replace("%player%", p.getDisplayName()))));
	}

}
