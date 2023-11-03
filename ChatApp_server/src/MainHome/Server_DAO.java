package MainHome;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Server_DAO {
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String user = "c##chatapp";
	String password = "chatapp1234";

	private Connection con;
	private Statement stmt;
	private ResultSet rs;

	public ArrayList<Server_VO> Login(String id) {
		ArrayList<Server_VO> login = new ArrayList<Server_VO>();
		try {
			connDB();

			String query = "SELECT * FROM login";
			if (id != null) {
				query += " where user_id='" + id + "'";
			}
			rs = stmt.executeQuery(query);
			rs.last();
			System.out.println("SQL : " + query);
			System.out.println("rs.getRow() : " + rs.getRow());

			if (rs.getRow() == 0) {
				System.out.println("0 row selected .....");
			} else {
				System.out.println(rs.getRow() + " rows selected.....");
				rs.previous();
				while (rs.next()) {
					String pwd = rs.getString("user_pwd");
					System.out.println(pwd);
					Server_VO data = new Server_VO(id, pwd);
					login.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("-------------------------------------------------------");
		}
		return login;
	}

	public ArrayList<Server_VO> pwdUp(String id, String name) {
		ArrayList<Server_VO> Pwdup = new ArrayList<Server_VO>();
		try {
			connDB();

			String query = "SELECT cn, name FROM emp WHERE cn='" + id + "' and name ='" + name + "'";
			System.out.println("dd");
			ResultSet rs3 = stmt.executeQuery(query);
			rs3.last();
			System.out.println("SQL : " + query);
			System.out.println("rs3.getRow() : " + rs3.getRow());
			if (rs3.getRow() == 0) {
				System.out.println("0 row selected .......");
			} else if(rs3.getRow()==1){
				System.out.println(rs3.getRow() + "rosw selected......");
				rs3.previous();
				
				while (rs3.next()) {
					String cn = rs3.getString("cn");
					String naMe = rs3.getString("name");
					
					Server_VO pwdup = new Server_VO(cn, naMe);
					Pwdup.add(pwdup);
				}
				query = "UPDATE login SET USER_PWD = '1111' WHERE USER_ID ='" + id + "'";
				rs3 = stmt.executeQuery(query);
				System.out.println("초기화 성공");
			}else {
				System.out.println("초기화 실패");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("패스워드 초기화 오류");
		}
		return Pwdup;
	}

	public ArrayList<Server_VO> user_Profile(String id) {
		ArrayList<Server_VO> Profile = new ArrayList<Server_VO>();
		try {
			connDB();

			String query = "SELECT * FROM emp where cn ='" + id + "'";
			ResultSet rs2 = stmt.executeQuery(query);
			rs2.last();
			System.out.println("SQL : " + query);
			System.out.println("rs2.getRow() : " + rs2.getRow());
			if (rs2.getRow() == 0) {
				System.out.println("0 row selected .....");
			} else {
				System.out.println(rs2.getRow() + " rows selected.....");
				rs2.previous();
				while (rs2.next()) {
					String name = rs2.getString("name");
					String email = rs2.getString("email");
					String phone = rs2.getString("phone");
					String dept_num = rs2.getString("dept_num");

					Server_VO profile = new Server_VO(name, email, phone, dept_num);
					Profile.add(profile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("-------------------------------------------------------");
		}
		return Profile;
	}

	public ArrayList<Server_VO> toDoList(String id) {
		ArrayList<Server_VO> todoList = new ArrayList<Server_VO>();
		String state = "";
		int index = 1;
		try {
			connDB();
			String query = "SELECT * FROM TODO where cn = '" + id + "'";
			ResultSet toDoResultSet = stmt.executeQuery(query);
			toDoResultSet.last();

			System.out.println("SQL : " + query);
			System.out.println("toDoResultSet.getRow() : " + toDoResultSet.getRow());

			if (state.equals("add")) {
				query = "insert into todo values ('" + id + "',  '내용', '완료여부', "+ index +"";
				toDoResultSet = stmt.executeQuery(query);
				toDoResultSet.last();

				System.out.println("SQL : " + query);
			} else if (state.equals("delete")) {
				query = "delete from todo where cn = '" + id + "' and todoindex = '" + index + "'";
				toDoResultSet = stmt.executeQuery(query);
				toDoResultSet.last();

				System.out.println("SQL : " + query);
			} else {
				if (toDoResultSet.getRow() == 0) {
					System.out.println("0 row selected .....");
				} else {
					System.out.println(toDoResultSet.getRow() + " rows selected.....");
					toDoResultSet.previous();
					while (toDoResultSet.next()) {
//						String cn = toDoResultSet.getString("cn");
						String doing = toDoResultSet.getString("doing");
						String done = toDoResultSet.getString("done");
						int toDoIndex = toDoResultSet.getInt("todoindex");

						Server_VO todolist = new Server_VO(id, doing, done, toDoIndex);
						todoList.add(todolist);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return todoList;
	}

	public ArrayList<Server_VO> setProfile(String id, String pwd, String phone, String email){
		ArrayList<Server_VO> setProfile = new ArrayList<Server_VO>();
		try {
			connDB();
			String setPwd, setPhone, setEmail;
			if(!pwd.equals(null)) {
				setPwd= pwd;
				String query1 = "UPDATE login SET USER_PWD = '"+pwd+"' WHERE USER_ID ='"+id+"'";
				ResultSet rsSetProfile = stmt.executeQuery(query1);
			}
			if(!phone.equals(null)) {
				setPhone=phone;
				String query2 = "UPDATE emp SET PHONE = '"+phone+"' WHERE CN ='"+id+"'";
				ResultSet rsSetProfile = stmt.executeQuery(query2);
			}
			if(!email.equals(null)) {
				setEmail = email;
				String query3 = "UPDATE emp SET EMAIL = '"+email+"' WHERE CN ='"+id+"'";
				ResultSet rsSetProfile = stmt.executeQuery(query3);
			}
			System.out.println(pwd+"=="+phone+"=="+email);
			Server_VO data = new Server_VO(pwd,phone,email);
			setProfile.add(data);

		} catch(Exception e) {
			e.printStackTrace();
			
		}
		return setProfile;
	}
	
	public void connDB() {
		try {
			Class.forName(driver);
			System.out.println("jdbc driver loading success.");
			con = DriverManager.getConnection(url, user, password);
			System.out.println("oracle connection success.");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			System.out.println("statement create success.");
			System.out.println("-------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
