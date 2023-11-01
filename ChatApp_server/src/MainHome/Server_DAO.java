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
			System.out.println("SQL : " + query);
			rs = stmt.executeQuery(query);
			rs.last();
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
	
	public ArrayList<Server_VO> pwdUp(String id, String name){
		ArrayList<Server_VO> Pwdup = new ArrayList<Server_VO>();
		try {
			connDB();
			
			String query = "SELECT name, cn FROM emp WHERE cn='" + id + "' and '"+name+"'";
			System.out.println("SQL : " + query);
			ResultSet rs3 = stmt.executeQuery(query);
			rs3.last();
			System.out.println("rs3.getRow() : "+rs3.getRow());
			if(rs3.getRow() == 0) {
				System.out.println("0 row selected .......");
			} else {
				System.out.println(rs3.getRow()+"rosw selected......");
				rs3.previous();
				while(rs3.next()) {
					String naMe = rs3.getString("name");
					String cn = rs3.getString("cn");
					Server_VO pwdup = new Server_VO(naMe,cn);
					Pwdup.add(pwdup);
					rs3.close();
					if(name.equals(naMe) && id.equals(cn)) {
						query = "UPDATE login SET USER_PWD = '1111' WHERE USER_ID ='"+id+"'";
						rs3 = stmt.executeQuery(query);
						rs3.last();
						System.out.println("초기화 성공");
					} else {
						System.out.println("초기화 실패!");
					}
				}
			}
		} catch (Exception e) {
			System.out.println("패스워드 초기화 오류");
		}
		return Pwdup;
	}
	
	public ArrayList<Server_VO> user_Profile(String id) {
		ArrayList<Server_VO> Profile = new ArrayList<Server_VO>();
//		String name = null, email = null, phone = null, dept_num = null;
		try {
			connDB();
			
			String query = "SELECT * FROM emp where cn ='" + id + "'";
			System.out.println("SQL : " + query);
			ResultSet rs2 = stmt.executeQuery(query);
			rs2.last();
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
