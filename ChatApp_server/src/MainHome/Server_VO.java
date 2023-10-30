package MainHome;

public class Server_VO {
	private String id, pwd;
	private String name, email, phone;
	private int dept_num;
	
	public Server_VO() { }
	
	public Server_VO(String id, String pwd) {
		this.id = id;
		this.pwd = pwd;
	}
	
	public Server_VO(String name, String email, String phone, int dept_num) {
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.dept_num = dept_num;
	}
	
	public String getID() {
		return id;
	}
	
	public String getPWD() {
		return pwd;
	}
	
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	public String getPhone() {
		return phone;
	}
	public int getDept_num() {
		return dept_num;
	}
}
