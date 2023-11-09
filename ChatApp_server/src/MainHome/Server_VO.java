package MainHome;

import java.awt.Image;

public class Server_VO {
	private String id, pwd;
	private String name, email, phone,dept_num, doing, image;
	private int todoindex;
	
	public Server_VO() { }
	
	public Server_VO(String name) {
		this.name = name;	
	}
	
	public Server_VO(String id, String pwd) {
		this.id = id;
		this.pwd = pwd;
	}
	public Server_VO(String pwd, String phone, String email) {
		this.pwd = pwd;
		this.phone = phone;
		this.email = email;
	}
	
	public Server_VO(String name, String email, String phone, String dept_num, String image) {
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.dept_num = dept_num;
		this.image = image;
	}

	public String getID() {return id;}
	
	public String getPWD() {return pwd;}
	
	public String getName() {return name;}
	
	public String getEmail() {return email;}
	
	public String getPhone() {return phone;}
	
	public String getDept_num() {return dept_num;}
	
	public String getDoing() {return doing;}
	
	public String getImage() {return image;}
	
	public int toDoIndex() {return todoindex;}
}
