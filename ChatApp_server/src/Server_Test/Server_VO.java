package Server_Test;

public class Server_VO {
	private String id, pwd;
	public Server_VO() { }
	
	public Server_VO(String id, String pwd) {
		this.id = id;
		this.pwd = pwd;
	}
	
	public String getID() {
		return id;
	}
	
	public String getPWD() {
		return pwd;
	}
}
