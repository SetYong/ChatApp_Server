package Server_Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcTest {
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String user = "c##chatapp";
	String password = "chatapp1234";

	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	
	public static void main(String[] args) {
		selectPos();
	}
	
	public void selectPos() {
		try {
			connDB();

			String SQL = "SELECT office From emp_pos order by office";
			System.out.println("SQL : " + SQL);
			rs = stmt.executeQuery(SQL);
		} catch(Exception e) {
			e.printStackTrace();
			while(rs.next()) {
				String pos = rs.getString("Pos");
			}
		}
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
