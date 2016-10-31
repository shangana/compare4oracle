package compare.context;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import compare.beans.definition.XMLConfig;

/**
 * @author   yueshanfei
 * @date  2016年9月5日
 */
public abstract class Compare {
    protected static final Logger logger = LogManager.getLogger();
    
    protected XMLConfig xmlConfig;
    
    protected String config;
    
    public Compare(String config) {
        this.config = config;
        if (!readConfigFile()) {
            return ;
        }
    }
    
    private boolean readConfigFile() {
        boolean exists = checkExistsConfigFile();
        if (!exists) {
            logger.error(config + ", this config is not exists!");
            return false;
        }
        //TODO 读取配置文件,并赋值
        Element root = getRootElement();
        this.xmlConfig = xmlConfigReader(root);
        
       return verifyXMLConfig();
    }
    
    protected Element getRootElement() {
        SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(config);
            return document.getRootElement();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    protected abstract boolean verifyXMLConfig();
    
    protected abstract XMLConfig xmlConfigReader(Element root);
    
    private boolean checkExistsConfigFile() {
        File file = new File(config);
        if (!file.exists()) {
            logger.error(config + ":is not exist");
            return false;
        }
        if (!file.isFile()) {
            logger.error(config + ":is not a file");
            return false;
        }
        return true;
    }
}