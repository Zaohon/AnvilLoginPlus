package cn.BlockMC.Zao_hon;

public class PoolConfig {
	private String driverclassname;
	private String jdbcurl;
	private String username;
	private String password;
	private int maxconnection = 20;
	private Long waittimeout;
	private String testquery = "SELECT * FROM DUAL";

	public void setDriverClassName(String n) {
		this.driverclassname = n;
	}

	public String getDriverClassName() {
		return this.driverclassname;
	}

	public void setJDBCUrl(String url) {
		this.jdbcurl = url;
	}

	public String getJDBCUrl() {
		return this.jdbcurl;
	}

	public void setUserName(String n) {
		this.username = n;

	}

	public String getUserName() {
		return this.username;
	}

	public void setPassword(String passwrd) {
		this.password = passwrd;
	}

	public String getPassworld() {
		return this.password;
	}

	public void setMaxConnection(int c) {
		this.maxconnection = c;
	}

	public int getMaxConnection() {
		return this.maxconnection;
	}


	public void setWaitTimeOut(Long t) {
		this.waittimeout = t;
	}

	public Long getWaitTimeOut() {
		return this.waittimeout;
	}
	public void setTestQuery(String query){
		this.testquery = query;
	}
	public String getTestQuery(){
		return this.testquery;
	}

}
