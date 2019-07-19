package cn.BlockMC.Zao_hon;

public class PlayerAuthInfo {
	private String name;
	private String uuid;
	private String password;
	private String firstLogin;
	private String lastLogin;
	private String ip;
	
	public PlayerAuthInfo(String name, String uuid,String password,String firstLogin,String lastLogin,String ip){
		this.name = name;
		this.uuid = uuid;
		this.password = password;
		this.firstLogin = firstLogin;
		this.lastLogin = lastLogin;
		this.ip = ip;
		
	}
	public String getName(){
		return name;
	}
	public String getPassword(){
		return password;
	}
	public String getUUID(){
		return uuid;
	}
	public String getFirstLogin(){
		return firstLogin;
	}
	public String getLastLogin(){
		return lastLogin;
	}
	public String getIP(){
		return ip;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public void setLastLogin(String lastLogin){
		this.lastLogin = lastLogin;
	}
	public void setIP(String ip){
		this.ip = ip;
	}
	
}
