package MainHome;

public class Server_VO {
	private String id, pwd;
	private String name, email, phone,dept_num, doing, done;
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
	
	public Server_VO(String name, String email, String phone, String dept_num) {
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.dept_num = dept_num;
	}
	
	public Server_VO(String id, String doing, String done, int todoindex) {
		this.id = id;
		this.doing = doing;
		this.done = done;
		this.todoindex = todoindex;
	}
	
	public String getID() {return id;}
	
	public String getPWD() {return pwd;}
	
	public String getName() {return name;}
	
	public String getEmail() {return email;}
	
	public String getPhone() {return phone;}
	
	public String getDept_num() {return dept_num;}
	
	public String getDoing() {return doing;}
	
	public String getDone() {return done;}
	
	public int toDoIndex() {return todoindex;}
}
