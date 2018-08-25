package cn.BlockMC.Zao_hon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.entity.Player;

//import com.zaxxer.hikari.HikariConfig;

public class Mysql {
	private final AnvilLogin plugin;
	private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS Users (Name VARCHAR(40),UUID VARCHAR(40),Password VARCHAR(30),FirstLogin VARCHAR(30),LastLogin VARCHAR(30),IP VARCHAR(30))";
	private static final String INSERT_NEW_USER = "INSERT INTO Users VALUES(?,?,?,?,?,?)";
	private static final String UPDATE_USER = "UPDATE Users Set LastLogin = ?,IP= ? WHERE UUID = ?";
	private static final String UPDATE_USER_PASSWORD = "UPDATE Users Set Password = ? WHERE UUID = ?";
	private static final String SELECT_USER_PASSWORD = "SELECT Password FROM Users WHERE UUID = ?";
	private static final String SELECT_USER_IP = "SELECT IP FROM Users WHERE UUID = ?";
	private static final String SELECT_USERS_FROM_IP = "SELECT UUID FROM Users WHERE IP = ?";
	private static final String SELECT_USERS_FROM_UUID = "SELECT * FROM Users WHERE UUID = ?";
	private ConnectionPool pool;
	// private Connection conn;

	public void updatePlayerPassword(Player p, String passwrd) {
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(UPDATE_USER_PASSWORD);
			s.setString(1, passwrd);
			s.setString(2, p.getUniqueId().toString());
			s.execute();
			// conn.close();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getPlayerIP(Player p) {
		String ip = "";
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(SELECT_USER_IP);
			s.setString(1, p.getUniqueId().toString());
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				ip = rs.getString(1);
			}
			// conn.close();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ip;
	}

	public void updatePlayer(Player p) {
		try {
			String ip1 = getPlayerIP(p);
			String ip2 = p.getAddress().getAddress().getHostName();
			if (!ip1.equals(ip2)) {
				plugin.getLogger().info("§c玩家" + p.getName() + "的IP地址与之前不符！请注意！");
				plugin.getLogger().info("以前是:" + ip1 + ",现在是:" + ip2);
				Connection conn = pool.getConnection();
				PreparedStatement s = conn.prepareStatement(UPDATE_USER);
				s.setString(1, getPresentTime());
				s.setString(2, p.getAddress().getAddress().getHostName());
				s.setString(3, p.getUniqueId().toString());
				s.execute();
				// conn.close();
				pool.releaseConnection(conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	public void playerRegisted(Player p, String password) {
		try {
			Connection conn = pool.getConnection();

			String name = p.getName();
			String uuid = p.getUniqueId().toString();
			String presenttime = getPresentTime();
			String ip = p.getAddress().getAddress().getHostName();
			PreparedStatement s = conn.prepareStatement(INSERT_NEW_USER);
			s.setString(1, name);
			s.setString(2, uuid);
			s.setString(3, password);
			s.setString(4, presenttime);
			s.setString(5, presenttime);
			s.setString(6, ip);
			s.execute();
			// conn.close();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isRegistered(UUID uuid) {
		boolean b = false;
		;
		try {
			Connection conn = pool.getConnection();

			PreparedStatement s = conn.prepareStatement(SELECT_USERS_FROM_UUID);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();
			rs.last();
			b = rs.getRow() > 0 ? true : false;
			rs.close();
			// conn.close();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}

	public int getUsersNumber(String ip) {
		int i = 0;
		try {
			Connection conn = pool.getConnection();

			PreparedStatement s = conn.prepareStatement(SELECT_USERS_FROM_IP);
			s.setString(1, ip);
			ResultSet rs = s.executeQuery();
			rs.last();
			i = rs.getRow();
			rs.close();
			// conn.close();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public String selectPlayerPassword(Player p) {
		String password = "";
		try {
			Connection conn = pool.getConnection();

			PreparedStatement s = conn.prepareStatement(SELECT_USER_PASSWORD);
			s.setString(1, p.getUniqueId().toString());
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				password = rs.getString(1);
			}
			// conn.close();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return password;
	}

	public Mysql(AnvilLogin plugin) {
		// pool = new ConnectionPoolManager(plugin);
		String hostname = plugin.getConfig().getString("MYSQL.Host");
		String port = plugin.getConfig().getString("MYSQL.Port");
		String database = plugin.getConfig().getString("MYSQL.DatabaseName");
		String username = plugin.getConfig().getString("MYSQL.UserName");
		String password = plugin.getConfig().getString("MYSQL.Password");
		PoolConfig config = new PoolConfig();
		config.setJDBCUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false"

		);
		config.setDriverClassName("com.mysql.jdbc.Driver");
		config.setUserName(username);
		config.setPassword(password);
		pool = new ConnectionPool(config);
		this.plugin = plugin;
		makeTable();
	}

	private void makeTable() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(CREATE_USERS_TABLE);
			ps.executeUpdate();

		} catch (SQLException e) {
		} finally {
			// pool.close(conn, ps, null);
			pool.releaseConnection(conn);
		}
	}

	public void onDisable() {
		// pool.closePool();
		pool.close();
	}

	private String getPresentTime() {
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
