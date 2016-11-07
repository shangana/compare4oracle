package compare.context;

import java.sql.Connection;
import java.sql.SQLException;

import compare.beans.definition.Database;

/**
 * @author   yueshanfei
 * @date  2016年11月2日
 */
public class OwnerParam {

    private Database database;
    private Database source;
    private Connection databaseConnection;
    private Connection sourceConnection;
    public Connection getDatabaseConnection() {
        databaseConnection= DatabaseCompare.getInstance().getConnection(database);
        return databaseConnection;
    }
    public Connection getSourceConnection() {
        sourceConnection = DatabaseCompare.getInstance().getConnection(source);
        return sourceConnection;
    }
    
    public void closeConnection() {
        if (null != sourceConnection) {
            try {
                sourceConnection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (null != databaseConnection) {
            try {
                databaseConnection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public Database getDatabase() {
        return database;
    }
    public void setDatabase(Database database) {
        this.database = database;
    }
    public Database getSource() {
        return source;
    }
    public void setSource(Database source) {
        this.source = source;
    }
    
}
