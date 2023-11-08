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

	public void newUser(String name, String cn, String dept){
		try {
			String sequery = "SELECT dept_ID from DEPARTMENT where dept_name = '"+dept+"'";
			rs = stmt.executeQuery(sequery);
			rs.last();
			int deptNum = rs.getInt("dept_ID");

			System.out.println("SELECT SQL : " + sequery);
			System.out.println("rs.getRow() : " + rs.getRow());
			
			String query = "Insert into emp Values ('"+ cn +"', '"+name+"', '000@000.000', '010-0000-0000', "+deptNum+", 00,0,'image')";
			stmt.executeQuery(query);
			
			System.out.println("emp SQL : " + query);
			
			query = "Insert into login Values ('"+ cn +"', 1234)";
			stmt.executeQuery(query);

			System.out.println("login SQL : " + query);
			
			query = "SELECT OFFICE FROM EMP_POS WHERE DEPTID="+deptNum+"";
			rs = stmt.executeQuery(query);
			rs.last();
			
			System.out.println("emp_pos SELECT SQL : " + query);
			
			String office = rs.getString("OFFICE");
			query = "Insert into emp_pos Values('팀원','"+cn+"','"+office+"',"+deptNum+")";
			stmt.executeQuery(query);

			System.out.println("emp_pos SQL : " + query);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

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
			} else if (rs3.getRow() == 1) {
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
			} else {
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

	public ArrayList<Server_VO> toDoList(String id, String doing, String state) {
		ArrayList<Server_VO> todoList = new ArrayList<Server_VO>();
		
		try {
			connDB();
			String query = "SELECT * FROM TODO where cn = '" + id + "'";
			ResultSet toDoResultSet = stmt.executeQuery(query);
			toDoResultSet.last();
			System.out.println("SQL : " + query);
			System.out.println("toDoResultSet.getRow() : " + toDoResultSet.getRow());

			if (state.equals("add")) {
				query = "insert into todo values ('" + id + "',  '"+doing+"')";
				toDoResultSet = stmt.executeQuery(query);

				System.out.println("SQL : " + query);
			} else if (state.equals("delete")) {
				query = "delete from todo where cn = '" + id + "' and  doing= '"+ doing +"'" ;
				toDoResultSet = stmt.executeQuery(query);

				System.out.println("SQL : " + query);
//			} else {
//				if (toDoResultSet.getRow() == 0) {
//					System.out.println("0 row selected .....");
//				} else {
//					System.out.println(toDoResultSet.getRow() + " rows selected.....");
//					toDoResultSet.previous();
//					while (toDoResultSet.next()) {
//						String doIng = toDoResultSet.getString("doing");
//						
//						Server_VO todolist = new Server_VO(id, doIng);
//						todoList.add(todolist);
//					}
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return todoList;
	}

	public String receivetoDoList(String id){
		String ab = null;
		try {
			connDB();
			String query = "SELECT doing FROM TODO where cn = '" + id + "'";
			ResultSet toDoResultSet = stmt.executeQuery(query);
			toDoResultSet.beforeFirst();
			System.out.println("SQL : " + query);
			System.out.println("toDoResultSet.getRow() : " + toDoResultSet.getRow());
			StringBuilder sb = new StringBuilder();
			while(toDoResultSet.next()) {
				String todorsStr = toDoResultSet.getString("doing");
				System.out.println("reStr" + todorsStr);
				sb.append(todorsStr + "//");
			}
			ab = sb.toString();
			System.out.println("retodo : " + ab);
		}	catch (Exception e ) {
			e.printStackTrace();
		}
		return ab;
	}
	
	public ArrayList<Server_VO> setProfile(String id, String pwd, String phone, String email) {
		ArrayList<Server_VO> setProfile = new ArrayList<Server_VO>();
		try {
			connDB();
			String setPwd, setPhone, setEmail;
			if (!pwd.equals(null)) {
				setPwd = pwd;
				String query1 = "UPDATE login SET USER_PWD = '" + pwd + "' WHERE USER_ID ='" + id + "'";
				ResultSet rsSetProfile = stmt.executeQuery(query1);
			}
			if (!phone.equals(null)) {
				setPhone = phone;
				String query2 = "UPDATE emp SET PHONE = '" + phone + "' WHERE CN ='" + id + "'";
				ResultSet rsSetProfile = stmt.executeQuery(query2);
			}
			if (!email.equals(null)) {
				setEmail = email;
				String query3 = "UPDATE emp SET EMAIL = '" + email + "' WHERE CN ='" + id + "'";
				ResultSet rsSetProfile = stmt.executeQuery(query3);
			}
			System.out.println(pwd + "==" + phone + "==" + email);
			Server_VO data = new Server_VO(pwd, phone, email);
			setProfile.add(data);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return setProfile;
	}

	public ArrayList<Server_VO> nameTree(String user_name) {
		ArrayList<Server_VO> nameTree = new ArrayList<Server_VO>();
		try {
			connDB();

			String query = "SELECT name FROM EMP";
			if (user_name != null) {
				query += " WHERE name LIKE '%" + user_name+ "%'";
			}
			ResultSet rsTree = stmt.executeQuery(query);
			rsTree.last();
			System.out.println("SQL : " + query);
			System.out.println("rs2.getRow() : " + rsTree.getRow());
			
			if (rsTree.getRow() == 0) {
				System.out.println("0 row selected .....");
			} else {
				System.out.println(rsTree.getRow() + " rows selected.....");
				rsTree.beforeFirst();
				while (rsTree.next()) {
					System.out.println("");
					String name = rsTree.getString("name");
					System.out.println("dao rsTree while : "+ name);
					Server_VO data = new Server_VO(name);
					nameTree.add(data);
				}
			}
			if(rsTree!=null) rsTree.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nameTree;
	}
	
	public ArrayList<Server_VO> deptList() {
		ArrayList<Server_VO> deptList = new ArrayList<Server_VO>();
		try {
			connDB();

			String query = "SELECT DEPT_NAME FROM DEPARTMENT";
			ResultSet rsDept = stmt.executeQuery(query);
			rsDept.last();
			System.out.println("SQL : " + query);
			System.out.println("rs2.getRow() : " + rsDept.getRow());
			
			if (rsDept.getRow() == 0) {
				System.out.println("0 row selected .....");
			} else {
				System.out.println(rsDept.getRow() + " rows selected.....");
				rsDept.beforeFirst();
				while (rsDept.next()) {
					System.out.println("");
					String deptName = rsDept.getString("DEPT_NAME");
					System.out.println("dao rsTree while : "+ deptName);
					Server_VO data = new Server_VO(deptName);
					deptList.add(data);
				}
			}
			if(rsDept!=null) rsDept.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deptList;
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
