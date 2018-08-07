package cn.BlockMC.Zao_hon;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

public class AnvilLogin extends JavaPlugin{
	private HashSet<UUID> logged = new HashSet<UUID>();
	private Mysql mysql = null;
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.reloadConfig();
		this.getLogger().info("--------AnvilLogin-------");
		this.getLogger().info("Version: 0.1");
		this.getLogger().info("Author: Zao_hon");
		this.getLogger().info("--------------------------");
		mysql = new Mysql(this);
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
		this.getCommand("changepassword").setExecutor(new Commands(this));

		BookUtil.initialize(this);
	}
	@Override
	public void onDisable(){
		mysql.onDisable();
	}
	public Mysql getSqlManager(){
		return mysql;
	}
	public HashSet<UUID> getLoggedPlayer(){
		return logged;
	}

}
	
