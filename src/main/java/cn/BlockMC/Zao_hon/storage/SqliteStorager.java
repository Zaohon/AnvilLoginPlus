package cn.BlockMC.Zao_hon.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.annotation.Nonnull;

import cn.BlockMC.Zao_hon.AnvilLoginPlus;
import cn.BlockMC.Zao_hon.PlayerAuthInfo;
import cn.BlockMC.Zao_hon.storage.DataStorager.PreparedStatementType;

public class SqliteStorager extends DataStorager {
	private AnvilLoginPlus plugin;
	private String path;

	public SqliteStorager(AnvilLoginPlus plugin) {
		this.plugin = plugin;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			plugin.PR("加载JDBC数据库失败");
			plugin.onDisable();
		}
		path = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + "users.db";
		this.setupTable();
	}

	public void setupTable() {
		Connection connection = setupConnection();
		try {
			Statement stat = connection.createStatement();
			stat.execute(
					"CREATE TABLE IF NOT EXISTS Users (Name VARCHAR(40),UUID VARCHAR(40) PRIMARY KEY NOT NULL,Password VARCHAR(30),FirstLogin VARCHAR(30),LastLogin VARCHAR(30),IP VARCHAR(30))");
			stat.close();
			connection.commit();
			connection.close();
		} catch (SQLException e) {
			plugin.PR("创建初始表失败");
			e.printStackTrace();
		}
	}

	@Override
	public Connection setupConnection() {
		try {
			Connection conn = DriverManager.getConnection(path);
			conn.setAutoCommit(false);
			return conn;
		} catch (SQLException e) {
			plugin.PR("连接数据库失败");
			plugin.onDisable();
			return null;
		}
	}

	@Override
	public PlayerAuthInfo getPlayerAuthInfo(UUID uuid) {
		PlayerAuthInfo info = null;
		try {
			Connection conn = setupConnection();
			setupPreparedStatement(conn, PreparedStatementType.SELECT_INFO);
			mSelectInfo.setString(1, uuid.toString());
			ResultSet rs = mSelectInfo.executeQuery();
			if (rs.next()) {
				String name = rs.getString(1);
				String password = rs.getString(2);
				String firstLogin = rs.getString(3);
				String lastLogin = rs.getString(4);
				String IP = rs.getString(5);
				info = new PlayerAuthInfo(name, uuid.toString(), password, firstLogin, lastLogin, IP);
			}
			mSelectInfo.close();
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return info;
	}

	@Override
	public void setPlayerInfo(UUID uuid, PlayerAuthInfo info) {
		try {
			Connection conn = setupConnection();
			setupPreparedStatement(conn, PreparedStatementType.REPLACE_INFO);
			mReplaceInfo.setString(1, info.getName());
			mReplaceInfo.setString(2, info.getUUID());
			mReplaceInfo.setString(3, info.getPassword());
			mReplaceInfo.setString(4, info.getFirstLogin());
			mReplaceInfo.setString(5, info.getLastLogin());
			mReplaceInfo.setString(6, info.getIP());
			mReplaceInfo.execute();
			mReplaceInfo.close();
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void setupPreparedStatement(Connection conn,PreparedStatementType type) throws SQLException {
		switch (type) {

		case SELECT_INFO:
			mSelectInfo = conn
					.prepareStatement("SELECT Name,Password,FirstLogin,LastLogin,IP FROM users WHERE UUID = ?");
			break;
		case REPLACE_INFO:
			mReplaceInfo = conn.prepareStatement("Replace INTO playerornaments VALUES(?,?,?,?,?,?)");
		case SELECT_IP_ACCOUNT:
			mSelectAccount = conn.prepareStatement("SELECT COUNT(1) FROM users WHERE IP = ?");
		default:
			break;

		}
	}

	@Override
	public int getIPAcoount(String ip) {
		int i = 0;
		try {
			Connection conn = setupConnection();
			setupPreparedStatement(conn, PreparedStatementType.SELECT_IP_ACCOUNT);
			mSelectAccount.setString(1, ip);
			ResultSet rs = mSelectAccount.executeQuery();
			if (rs.next()) {
				i = rs.getInt(1);
			}
			mSelectAccount.close();
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

}
