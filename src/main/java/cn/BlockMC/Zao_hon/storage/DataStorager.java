package cn.BlockMC.Zao_hon.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import cn.BlockMC.Zao_hon.PlayerAuthInfo;

public abstract class DataStorager {
	protected PreparedStatement mSelectInfo;
	protected PreparedStatement mReplaceInfo;
	protected PreparedStatement mSelectAccount;
	public abstract Connection setupConnection();
	public abstract PlayerAuthInfo getPlayerAuthInfo(UUID uuid);
	public abstract void setPlayerInfo(UUID uuid,PlayerAuthInfo info);
	public abstract int getIPAcoount(String ip);
	public abstract void setupPreparedStatement(Connection conn,PreparedStatementType type) throws SQLException;
	protected enum PreparedStatementType{
		SELECT_INFO,REPLACE_INFO,SELECT_IP_ACCOUNT;
	}
}
