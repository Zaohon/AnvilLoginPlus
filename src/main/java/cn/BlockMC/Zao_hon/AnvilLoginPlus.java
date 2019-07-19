package cn.BlockMC.Zao_hon;

import java.util.Calendar;
import java.util.HashSet;
import java.util.UUID;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import cn.BlockMC.Zao_hon.Util.BookUtil;
import cn.BlockMC.Zao_hon.commands.Commands;
import cn.BlockMC.Zao_hon.storage.DataStorager;
import cn.BlockMC.Zao_hon.storage.MysqlStorager;
import cn.BlockMC.Zao_hon.storage.SqliteStorager;

public class AnvilLoginPlus extends JavaPlugin {
	private HashSet<UUID> logged = new HashSet<UUID>();
	private DataStorager dataStorager;
	private PlayerAuthInfoManager playerAuthInfoManager;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.reloadConfig();
		PR("========================");
		PR("      AnvilLogin          ");
		PR("     Version: " + this.getDescription().getVersion());
		PR("     Author:Zao_hon           ");
		PR("========================");
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
		this.getCommand("changepassword").setExecutor(new Commands(this));

		BookUtil.initialize(this);
		
		switch(getConfig().getString("SQLType")){
			case "MYSQL":
				dataStorager = new MysqlStorager(this);
				break;
			case "SQLITE":
				dataStorager = new SqliteStorager(this);
		}
		playerAuthInfoManager = new PlayerAuthInfoManager(this);
		
		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("servers", () -> "Bungee"));

	}
	public void sendWelcomeMessage(Player p) {
		getConfig().getStringList("WelcomeMessage").forEach(m -> p
				.sendMessage(ChatColor.translateAlternateColorCodes('&', m.replace("%player%", p.getDisplayName()))));
	}

	

	public void PR(String str) {
		this.getLogger().info(str);
	}

	public HashSet<UUID> getLoggedPlayer() {
		return logged;
	}
	public DataStorager getDataStorager(){
		return dataStorager;
	}
	public PlayerAuthInfoManager getPlayerAuthInfoManager(){
		return playerAuthInfoManager;
	}
	public static String getPresentTime() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		return year + "." + month + "." + day + "-" + hour + ":" + minute + ":" + second;
	}

}
