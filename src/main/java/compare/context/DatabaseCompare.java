package compare.context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import compare.beans.DatabaseInfo;
import compare.beans.IndColumns;
import compare.beans.Index;
import compare.beans.TabColumn;
import compare.beans.Table;
import compare.beans.definition.Database;

/**
 * @author   yueshanfei
 * @date  2016年9月6日
 */
public class DatabaseCompare {
    protected static final Logger logger = LogManager.getLogger();
    
    public static DatabaseCompare getInstance() {
        return new DatabaseCompare();
    }
    
    public DatabaseInfo getDatabaseInfos(List<Database> databases) {
        DatabaseInfo info = new DatabaseInfo();
        for (Database database : databases) {
            DatabaseInfo d = getDatabaseInfo(database);
            info.getTables().putAll(d.getTables());
            info.getIndexs().putAll(d.getIndexs());
        }
        
        return info;
    }
    
    public DatabaseInfo getDatabaseInfo(Database config) {
        logger.debug("connection oracle user="+config.getDbusr()+" url="+config.getDburl());
        String driver = "oracle.jdbc.driver.OracleDriver";
        Connection connection = null;
        try {
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection(config.getDburl(), config.getDbusr(), config.getDbpwd());
            DatabaseInfo info = new DatabaseInfo();
            info.getTables().put(config.getDbusr().toUpperCase(), getTables(connection));
            info.getIndexs().put(config.getDbusr().toUpperCase(), getIndexs(connection));
            logger.debug("get database info finished.");
            return info;
        }
        catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            logger.error(e);
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            logger.error(e);
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            logger.error(e);
            e.printStackTrace();
        }
        finally {
            if (null != connection) 
            {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private List<String> getPrimaryKeys(Connection connection) {
        logger.debug("get primaryKeys.");
        String sql="select CONSTRAINT_NAME,TABLE_NAME from user_constraints where CONSTRAINT_TYPE='P'";
        ResultSet rs = null;
        Statement statement = null;
        List<String> keys = Lists.newArrayList();
        try {
            
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            while(rs.next()) {
                String name = rs.getString("CONSTRAINT_NAME");
                if (name.indexOf("$") != -1) {
                    continue;
                }
                keys.add(name);
            }
            logger.debug("get primaryKeys finished.");
            return keys;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (null != rs) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (null != statement) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    private TreeMap<String, Index> getIndexs(Connection connection) {
        logger.debug("get indexs.");
        String sql = "SELECT TABLE_NAME,INDEX_NAME,UNIQUENESS FROM USER_INDEXES";
        List<String> primaryKeys = getPrimaryKeys(connection);
        ResultSet rs = null;
        Statement statement = null;
        try {
            
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            Index index;
            TreeMap<String, Index> indexs = Maps.newTreeMap();
            LinkedHashMap<String, IndColumns> columns = getIndColumns(connection);
            while (rs.next()) {
                String indexName = rs.getString("INDEX_NAME");
                if (indexName.indexOf("$") != -1) {
                    continue;
                }
                index = new Index();
                if (primaryKeys.contains(indexName)) {
                    index.setPrimaryKey("PRIMARY KEY");
                } else if ("UNIQUE".equals(rs.getString("UNIQUENESS"))){
                    index.setUniqueness("UNIQUE");
                }
                index.setIndexName(indexName);
                index.setTableName(rs.getString("TABLE_NAME"));
                index.setColumnName(sortColumnName(columns.get(indexName)));
                indexs.put(indexName, index);
            }
            logger.debug("get indexs finished.");
            return indexs;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (null != rs) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (null != statement) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    private List<String> sortColumnName(IndColumns indcolumn) {
        List<String> sort = Lists.newArrayList();
        Map<Integer, String> map = indcolumn.getColumnNameMap();
        for (int ind = 1; ind <= indcolumn.getColumnName().size(); ind++) {
            sort.add(map.get(ind));
        }
        return sort;
    }
    private LinkedHashMap<String, IndColumns> getIndColumns(Connection connection) {
        logger.debug("get indexs column.");
        String sql = "SELECT INDEX_NAME,TABLE_NAME,COLUMN_NAME,COLUMN_POSITION FROM USER_IND_COLUMNS";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            LinkedHashMap<String, IndColumns> columns = Maps.newLinkedHashMap();
            while (rs.next()) {
                IndColumns column;
                String indexName = rs.getString("INDEX_NAME");
                if (columns.containsKey(indexName)) {
                    column = columns.get(indexName);
                }
                else {
                    column = new IndColumns();
                }
                column.setIndexName(indexName);
                column.setTableName(rs.getString("TABLE_NAME"));
                column.getColumnName().add(rs.getString("COLUMN_NAME"));
                column.getColumnNameMap().put(rs.getInt("COLUMN_POSITION"), rs.getString("COLUMN_NAME"));
                columns.put(indexName, column);
            }
            logger.debug("get indexs column finished.");
            return columns;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (null != rs) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (null != statement) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private TreeMap<String ,Table> getTables(Connection connection) {
        logger.debug("get tables.");
        String sql = "SELECT TABLE_NAME FROM USER_TABLES";
        ResultSet rs = null;
        try {
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            List<String> tableNames = Lists.newArrayList();
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName.indexOf("$") != -1) {
                    continue;
                }
                tableNames.add(tableName);
            }
            TreeMap<String, Table> tables = Maps.newTreeMap();
            Map<String, LinkedHashMap<String, TabColumn>> tabColumns = getTabColumns(connection);
            for (String tableName : tableNames) {
                Table table = new Table();
                table.setTableName(tableName);
                table.setColumns(tabColumns.get(tableName));
                tables.put(tableName, table);
                
            }
            logger.debug("get tables finished.");
            return tables;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    /**<String,TabColumns> <columnName,TabColumn>
     * @param connection
     * @return
     * @throws SQLException
     */
    private Map<String, LinkedHashMap<String, TabColumn>> getTabColumns(Connection connection) {
        logger.debug("get table coulumns.");
        String sql = "SELECT TABLE_NAME,COLUMN_NAME,NULLABLE,DATA_DEFAULT,DATA_TYPE,DATA_LENGTH,DATA_SCALE,DATA_PRECISION,COLUMN_ID,CHARACTER_SET_NAME,CHAR_LENGTH,CHAR_USED FROM USER_TAB_COLUMNS";
        Statement statement;
        ResultSet rs = null;
        
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            TabColumn column;
            Map<String, LinkedHashMap<String, TabColumn>> tables = Maps.newConcurrentMap();
            while (rs.next()) {
                column = new TabColumn();
                String tableName = rs.getString("TABLE_NAME");
                column.setTableName(tableName);
                String columName = rs.getString("COLUMN_NAME");
                column.setColumnName(columName);
                column.setNullable(rs.getString("NULLABLE"));
                column.setDataDefault(rs.getString("DATA_DEFAULT"));
                String dataType = rs.getString("DATA_TYPE");
                int length = rs.getInt("DATA_LENGTH");
                String scale = rs.getString("DATA_SCALE");
                String precision = rs.getString("DATA_PRECISION");
                column.setColumnId(rs.getString("COLUMN_ID"));
                column.setCharacterSetName(rs.getString("CHARACTER_SET_NAME"));
                String charUsed = rs.getString("CHAR_USED");
                String charLength = rs.getString("CHAR_LENGTH");
                String columnType = dataType;
                if ("CHAR".equals(dataType) || "VARCHAR".equals(dataType) || "VARCHAR2".equals(dataType)) {
                    if ("C".equals(charUsed)) {
                        columnType += "("+charLength+" char)";
                    }else {
                        columnType += "(" + length + ")";
                    }
                }
                else if (null != scale && Integer.parseInt(scale) > 0) {
                    columnType += "(" + precision + "," + scale + ")";
                }
                else if (null != scale && Integer.parseInt(scale) == 0) {
                    columnType += "(" + precision + ")";
                }
                else if (null != precision) {
                    columnType += "(" + precision + ")";
                }
                column.setColumnType(columnType);
                LinkedHashMap<String, TabColumn> columns;
                if (tables.containsKey(tableName)) {
                    columns = tables.get(tableName);
                }
                else {
                    columns = Maps.newLinkedHashMap();
                }
                columns.put(columName, column);
                tables.put(tableName, columns);
            }
            logger.debug("get table coulumns finished.");
            return tables;
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if (null != rs) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
        
    }
}
