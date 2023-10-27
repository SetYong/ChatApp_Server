import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrcleExample{
   public static void main(String[] args) {
      String driver = "oracle.jdbc.driver.OracleDriver";
      String url = "jdbc:oracle:thin:@localhost:1521:xe";
      String user = "c##chatapp";
      String password = "chatapp1234";
      
      try {
         Class.forName(driver);
         System.out.println("jdbc driver loading success.");
         Connection conn = DriverManager.getConnection(url, user, password);
         System.out.println("oracle connection success.\n");
         Statement stmt = conn.createStatement();

         String sql = "SELECT * FROM emp";
         ResultSet rs = stmt.executeQuery(sql);
         while(rs.next()) {
            System.out.print(rs.getString("cn")+"\t");
         }

      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      } catch (SQLException e) {
         e.printStackTrace();
      }

   }
}