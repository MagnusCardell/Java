import java.util.ResourceBundle;
import java.util.*; //For error messages
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.*; //For error messages
import java.io.*; //For input
public class TryJDBC {
	static ResourceBundle bundle;
	public static void main(String[] args) {
		
		try{
			bundle = ResourceBundle.getBundle("javaconfig");
		} catch (NullPointerException e){
			System.err.println("getBundle failed, NullPointer");
		} catch (MissingResourceException e) {
			System.err.println("getBundle failed, Missing Resource");
		}
		String user = "";
		String password = "";
		try{
			user = bundle.getString("jdbc.user");
			password = bundle.getString("jdbc.password");  
		} catch(NullPointerException e) {
			System.err.println("getString failed, key is NULL");
		} catch(MissingResourceException e) {
			System.err.println("getString failed, key not found");
		} catch(ClassCastException e) {
			System.err.println("getString failed, key is no string");
		}
		
		
		String url = bundle.getString("jdbc.url") + bundle.getString("jdbc.dbname");
		Connection con = null;
		try {
			Class.forName(bundle.getString("jdbc.driver")); 
			con = DriverManager.getConnection(url, user, password);
			System.out.println("Successful connection to DBMS with user "+ user);
		} catch(SQLException e) {
			System.err.println("Database error in GETCONNECTION");
			System.out.println(e.getErrorCode());
		} catch(Exception e) {
			System.err.println("FORNAME failed");
			System.err.println(e);
		}
		Statement stmt = null;
		int countRecords = 0;
		String collectResult = ""; //Holds SQL returns
		try {
			stmt = con.createStatement();
			String selectAction = "SELECT * FROM patron";
			ResultSet rs = stmt.executeQuery(selectAction);

			while(rs.next()) {
				String pid = rs.getString("pid");
				String name = rs.getString("name");
				int phone = rs.getInt("phone");
				collectResult +=pid + '\t'+ name + '\t' + phone + '\n';
				countRecords++;
			}
		} catch(SQLException e) {
			System.err.println("SQL operation failed");
			System.out.println(e.getErrorCode());
		}
		System.out.println(countRecords + " records found");
		System.out.println(collectResult);

		try {
                     	stmt = con.createStatement();
                        String createAction = "CREATE table mytmp (id int primary key, val text)";
                        String insertAction = "INSERT into mytmp VALUES (1, 'hello')";
			stmt.executeUpdate(createAction);
			stmt.executeUpdate(insertAction);
			System.out.println("Created mytmp and inserted values (1, 'hello')");			
		
                } catch(SQLException e) {
                        System.err.println("SQL operation failed in create tables");
                        System.out.println(e.getErrorCode());
                }		
			
		System.out.println("Performing user input and SQL injection \n");
		System.out.println("Enter a name to fetch data from");
		int strlen;
		int maxLen = 50;
		byte[] strline = new byte[maxLen];
		try {
			strlen = System.in.read(strline, 0, maxLen);
			String str = new String(strline, 0, strlen-1); // -1 to remove newline
			PreparedStatement ps1 = con.prepareStatement("SELECT name, phone FROM patron WHERE name ~*? ORDER BY name");
			ps1.setString(1, str);
			ResultSet rs = ps1.executeQuery();
			while(rs.next()) {
				String name = rs.getString("name");
				String phone;
				if (rs.getString("phone")==null) {
					phone = "";
				} else {
					phone = 'x' + rs.getString("phone");
				}
				System.out.println(name + '\t' + phone);	
			}
		} catch(SQLException e) {
			System.err.println("SQL operation failed");
			System.out.println(e.getErrorCode());
		} catch(IOException ioe) {
			System.err.println("Reading in failed");
		}
	}

}
