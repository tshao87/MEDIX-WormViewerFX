package annotationtoolfx.db;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.Connection;

public class ConnectionSingleton {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:postgresql://cdmmedixsrv.cdm.depaul.edu:5432/c_elegans_v6";

    //  Database credentials
//    static final String USER = "postgres";
 //   static final String PASS = "dbadmin";
    static final String USER = "annotationuser";
    static final String PASS = "celegans!";
    
    static ConnectionSingleton instance = null;
    
    private Connection conn = null;    
    private String lastLoginId = "";
    
    public boolean Login(String email, String password) {
		try {
			String sql = "SELECT * from userinfo WHERE emailid = ? AND password = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, password);
			lastLoginId = email;
			return stmt.executeQuery().next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
    }
    
    protected ConnectionSingleton(Connection c) {
    	conn = c;
    }
    
    public static ConnectionSingleton getConnectionInstance() {
    	
    	if(instance != null) {
    		try {
    			instance.getConnection().createStatement().executeQuery("SELECT * FROM userinfo;");
    		}
    		catch (SQLException e){
    			instance = null;
    		}
    	}

    	if(instance == null) {
    		try {
    			Class.forName("org.postgresql.Driver");
    			Properties props = new Properties();
    			props.setProperty("user",USER);
    			props.setProperty("password",PASS);
    	//		props.setProperty("sslmode","allow");
				instance = new ConnectionSingleton(DriverManager.getConnection(DB_URL, props));
				
    		}
    		catch(Exception e) {
    			//Should never happen in production
    			System.out.print(e.getMessage());
    		}
    	}
    	
    	return instance;
    }
    
    public Connection getConnection() {
    	return conn;
    }
    
    public void cleanup() {
    	try {
			conn.close();
		} catch (SQLException e) {
			// should not happen
			e.printStackTrace();
		}
    	instance = null;
    }

	public String getLastLoginId() {
		return lastLoginId;
	}
    
}
