package cn.BlockMC.Zao_hon.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import cn.BlockMC.Zao_hon.AnvilLoginPlus;

public class BookUtil {
	private static ItemStack book;
	// private static Method openbook;
	private static final String CraftItemStack = "inventory.CraftItemStack";

	public static void openBook(Player p) {
		try {
			ItemStack item = p.getInventory().getItemInMainHand();
			Object player = NMSManager.getHandle(p);
			Method openbook = NMSManager.getMethod("a", player.getClass(),
					new Class[] { NMSManager.getNMSClass("ItemStack"), NMSManager.getNMSClass("EnumHand") });

			Method asNMSCopy = NMSManager.getMethod("asNMSCopy", NMSManager.getCraftClass(CraftItemStack),
					new Class[] { ItemStack.class });
			Object nmsitem = asNMSCopy.invoke(CraftItemStack.getClass(), book);
			Object enumhand = NMSManager.getNMSClass("EnumHand").getEnumConstants()[0];
			p.getInventory().setItemInMainHand(book);
			openbook.invoke(player, nmsitem, enumhand);
			p.getInventory().setItemInMainHand(item);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			e1.printStackTrace();
		}

	}

	public static void initialize(AnvilLoginPlus plugin) {
		book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
//		YamlConfiguration bookconfig = YamlConfiguration.loadConfiguration(plugin.getBookFile());
//		bookconfig.getKeys(false).forEach(key->{
//			List<String> page = bookconfig.getStringList(key);
//			String str="";
//			page.forEach(m->{
//				str+=m;
//			});
//		});
		meta.addPage(plugin.getConfig().getString("BookMessage"));
		book.setItemMeta(meta);
	}

}
