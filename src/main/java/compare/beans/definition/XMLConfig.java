package compare.beans.definition;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;

import com.google.common.collect.Lists;

/**
 * @author   yueshanfei
 * @date  2016年9月21日
 */
public class XMLConfig {
    protected static final Logger logger = LogManager.getLogger();
    
    private Mail mail;
    
    private PDM pdm;
    
    private Database database;
    private List<Database> sources;
    private List<Database> databases;
    
    
    private Database singleDatabase;
    
    private PDM singlePDM;
    
    private Equality equality;
    
    public Mail getMail() {
        return mail;
    }
    
    @SuppressWarnings("unchecked")
    public void setMail(Element element) {
        Mail mail = new Mail();
        for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext();) {
            Element item = iterator.next();
            if ("stmp".equalsIgnoreCase(item.getName())) {
                mail.setStmp(item.getTextTrim());
            }
            else if ("user".equalsIgnoreCase(item.getName())) {
                mail.setUser(item.getTextTrim());
            }
            else if ("passwd".equalsIgnoreCase(item.getName())) {
                mail.setPasswd(item.getTextTrim());
            }
            else if ("receiver".equalsIgnoreCase(item.getName())) {
                mail.setReveicer(item.getTextTrim());
            }
        }
        
        this.mail = mail;
    }
    
    public PDM getPdm() {
        return pdm;
    }
    
    @SuppressWarnings("unchecked")
    public void setPdm(Element item) {
        PDM pdm = new PDM();
        for (Iterator<Element> iterator = item.elementIterator(); iterator.hasNext();) {
            Element element = iterator.next();
            if ("svn".equalsIgnoreCase(element.getName())) {
                pdm.setSvn(element);
            }
            else if ("local".equalsIgnoreCase(element.getName())) {
                pdm.setLocal(element);
            }
            else if ("submeter".equalsIgnoreCase(element.getName())) {
                pdm.setSubmeter("true".equalsIgnoreCase(element.getTextTrim()));
            }
        }
        
        this.pdm = pdm;
    }
    
    public Database getDatabase() {
        return database;
    }
    
    public void setDatabase(Element databasesElement) {
        Database db = new Database();
        String url = databasesElement.elementText("url");
        String username = databasesElement.elementText("username");
        String passwd = databasesElement.elementText("passwd");
        db.setDbpwd(passwd);
        db.setDburl(url);
        db.setDbusr(username);
        this.database = db;
    }
    
    public List<Database> getDatabases() {
        return databases;
    }
    
    @SuppressWarnings("unchecked")
    public void setDatabases(Element databasesElement) {
        
        List<Database> databases = Lists.newArrayList();
        for (Iterator<Element> iterator = databasesElement.elementIterator(); iterator.hasNext();) {
            Element databaseElement = iterator.next();
            if (!"database".equals(databaseElement.getName())) {
                logger.error("<" + databaseElement.getName() + "> 标签定义错误!");
                continue;
            }
            Database database = new Database();
            databases.add(database);
            Iterator<Element> element = databaseElement.elementIterator();
            while (element.hasNext()) {
                Element obj = element.next();
                if ("url".equals(obj.getName())) {
                    database.setDburl(obj.getTextTrim());
                }
                else if ("username".equals(obj.getName())) {
                    database.setDbusr(obj.getTextTrim());
                }
                else if ("passwd".equals(obj.getName())) {
                    database.setDbpwd(obj.getTextTrim());
                }
            }
        }
        this.databases = databases;
    }
    
    public Database getSingleDatabase() {
        return singleDatabase;
    }
    
    public PDM getSinglePDM() {
        return singlePDM;
    }
    
    public void setSingle(Element databasesElement) {
        Database db = new Database();
        String url = databasesElement.elementText("url");
        String username = databasesElement.elementText("username");
        String passwd = databasesElement.elementText("passwd");
        db.setDbpwd(passwd);
        db.setDburl(url);
        db.setDbusr(username);
        this.singleDatabase = db;
    }
    
    public void setSingle(PDM single) {
        this.singlePDM = single;
    }
    
    public Equality getEquality() {
        return null == equality ? new Equality() : equality;
    }
    
    @SuppressWarnings("unchecked")
    public void setEquality(Element element) {
        Equality equality = new Equality();
        List<String> usernames = Lists.newArrayList();
        equality.setUsernames(usernames);
        for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext();) {
            Element item = iterator.next();
            if ("username".equalsIgnoreCase(item.getName())) {
                if (null != item.getTextTrim() && !"".equals(item.getTextTrim())) {
                    usernames.add(item.getTextTrim().toUpperCase());
                }
            }
        }
        
        this.equality = equality;
    }

    public List<Database> getSources() {
        return sources;
    }

    @SuppressWarnings("unchecked")
    public void setSources(Element databasesElement) {
        List<Database> databases = Lists.newArrayList();
        for (Iterator<Element> iterator = databasesElement.elementIterator(); iterator.hasNext();) {
            Element databaseElement = iterator.next();
            if (!"database".equals(databaseElement.getName())) {
                logger.error("<" + databaseElement.getName() + "> 标签定义错误!");
                continue;
            }
            Database database = new Database();
            databases.add(database);
            Iterator<Element> element = databaseElement.elementIterator();
            while (element.hasNext()) {
                Element obj = element.next();
                if ("url".equals(obj.getName())) {
                    database.setDburl(obj.getTextTrim());
                }
                else if ("username".equals(obj.getName())) {
                    database.setDbusr(obj.getTextTrim());
                }
                else if ("passwd".equals(obj.getName())) {
                    database.setDbpwd(obj.getTextTrim());
                }
            }
        }
        this.sources = databases;
    }
    
}
