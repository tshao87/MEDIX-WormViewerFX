package singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mshao1
 */
public class ConnectionManager {
    private static final String DB_NAME = "c_elegans_v6";
    private static final String DB_URL = "jdbc:postgresql://cdmmedixsrv.cdm.depaul.edu:5432/" + DB_NAME;
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "dbadmin";

    private static ConnectionManager connectionManager = null;
    private Connection connection = null;

    static {
        connectionManager = new ConnectionManager();
    }

    private ConnectionManager() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            } catch (SQLException ex) {
                Logger.getLogger(PostgresSQLDBManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public final static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public final Connection getConnection() {
        return connection;
    }
}
