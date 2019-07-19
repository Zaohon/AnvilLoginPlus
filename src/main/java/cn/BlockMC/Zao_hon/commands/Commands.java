package cn.BlockMC.Zao_hon.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.BlockMC.Zao_hon.AnvilLoginPlus;

public class Commands implements CommandExecutor{
	private AnvilLoginPlus plugin;
	public Commands(AnvilLoginPlus plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//		if(cmd.getName().equalsIgnoreCase("changepassword")){
//			if(!(sender instanceof Player)){
//				sender.sendMessage("该指令只有玩家可以用");
//				return true;
//			}
//			Player p = (Player) sender;
//			int lenth = args.length;
//			if(lenth!=2){
//				p.sendMessage("§c/changepassword 旧密码 新密码");
//				return true;
//			}
//			String op1 = plugin.getSqlManager().selectPlayerPassword(p);
//			String op2 = args[0];
//			if(!op1.equals(op2)){
//				p.sendMessage("§c旧密码错误！");
//				plugin.getLogger().info(p.getName()+"尝试使用错误的密码更改密码！");
//				return true;
//			}else{
//				String np = args[1];
//				plugin.getSqlManager().updatePlayerPassword(p, np);
//				p.sendMessage("§a更换密码成功！新密码为§d"+np);
//				return true;
//			}
////			return true;
//		}
		return false;
	}

}
