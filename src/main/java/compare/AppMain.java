package compare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import compare.context.Database2Database;
import compare.context.PdmFile2Database;

/**
 * @author   yueshanfei
 * @date  2016年10月10日
 */
public class AppMain {
    private static final Logger logger = LogManager.getLogger();
    
    /**
     * args[0] xml的配置文件路径
     * args[1] 1-pdmTodatabase,  2-databaseTodatabase
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            logger.error("parameter is error.\r\n\t args[0] The xml config path. \r\n\t args[1] 1-pdmTodatabase,  2-databaseTodatabase");
            return;
        }
        try {
            if ("1".equals(args[1])) {
                pdm2Database(args);
            }
            else if ("2".equals(args[1])) {
                database2Database(args);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void database2Database(String[] args) throws Exception {
        final Database2Database database = new Database2Database(args[0]);
        database.compare();
    }
    
    private static void pdm2Database(String[] args) throws Exception {
        final PdmFile2Database database = new PdmFile2Database(args[0]);
        database.compare();
    }
    
}
