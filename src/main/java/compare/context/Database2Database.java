package compare.context;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.dom4j.Element;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import compare.beans.DatabaseInfo;
import compare.beans.Index;
import compare.beans.Table;
import compare.beans.definition.Database;
import compare.beans.definition.XMLConfig;
import compare.core.CompareResult;
import compare.core.MailHandler;


public class Database2Database extends Compare {
    private DatabaseCompare sourseCompare = DatabaseCompare.getInstance();
    private DatabaseCompare databaseCompare = DatabaseCompare.getInstance();

    private MailHandler handler;
    private Database database;
    private List<Database> databases;
    private Database single;
    
    private DatabaseInfo sourseInfo;
    private DatabaseInfo compareInfo;
    public Database2Database(String config) {
        super(config);
        handler = new MailHandler(xmlConfig.getMail());
        database = xmlConfig.getDatabase();
        Thread s = new Thread() {
            @Override
            public void run() {
                sourseInfo = sourseCompare.getDatabaseInfo(database);
            }
        };
        s.start();
        single = xmlConfig.getSingleDatabase();
        Thread c = new Thread() {
            @Override
            public void run() {
                if (null != single) {
                    logger.debug("--single compare config--");
                    compareInfo  = databaseCompare.getDatabaseInfo(single);
                    compareInfo.setSingle(true);
                    databases = Lists.newArrayList(single);
                } else {
                    logger.debug("--databases compare config--");
                    databases = xmlConfig.getDatabases();
                    compareInfo = databaseCompare.getDatabaseInfos(databases);
                }
            }
        };
        c.start();
        
        try {
            s.join();
            c.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean verifyXMLConfig() {
        Database database2 = xmlConfig.getDatabase();
        List<Database> databases = xmlConfig.getDatabases();
        if (null == database2 || null == databases) {
            logger.error(xmlConfig + ",this config is error!");
            return false;
        }

        logger.debug("verify xml config is successfully");
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected XMLConfig xmlConfigReader(Element root) {
        if (null == root || !"compare".equals(root.getName())) {
            logger.error("读取<compare>标签错误!");
            return null;
        }
        XMLConfig xml = new XMLConfig();
        for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext();) {
            Element item = (Element)iterator.next();
            String nodeName = item.getName();
            if ("mail".equals(nodeName)) {
                xml.setMail(item);
            }
            else if ("database".equals(nodeName)) {
                xml.setDatabase(item);
            }
            else if ("databases".equals(nodeName)) {
                xml.setDatabases(item);
            } else if ("single".equals(nodeName)) {
                xml.setSingle(item);
            }
        }
        return xml;
    }
    private void getEquality() throws Exception {
        
    }
    public void compareTable() throws IOException {

        logger.debug("compare table count Thread number is "+databases.size());
        List<DifferenceTable> errors = Lists.newArrayList();
        LinkedHashMap<String, TreeMap<String, Table>> source = sourseInfo.getTables();
        LinkedHashMap<String, TreeMap<String, Table>> compare = compareInfo.getTables();
        //起多少个线程
        CountDownLatch latch = new CountDownLatch(databases.size());
        
        for (Database database : databases) {
            CompareTable diff = new CompareTable(latch);
            if (compareInfo.isSingle()) {
                TreeMap<String, Table> c = Maps.newTreeMap();
                for (TreeMap<String, Table> treeMap : compare.values())
                {
                    c.putAll(treeMap);
                }
                diff.setCompare(c);
                

                TreeMap<String, Table> s = Maps.newTreeMap();
                for (TreeMap<String, Table> treeMap : source.values())
                {
                    s.putAll(treeMap);
                }
                diff.setSource(s);
                diff.setOwner("");
            }
            else {
                String owner = database.getDbusr();
                diff.setCompare(compare.get(owner));
                diff.setSource(source.get(owner));
                diff.setOwner(owner);
            }
            diff.setErrors(errors);
            diff.start();
        }
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.debug("compare table is finished.");
        int sourceNumber= source.values().size();
        int compareNumber = compare.values().size();

        CompareResult result = new CompareResult();
        result.setCompareContent("database");
        result.setCompareNumber(compareNumber);
        result.setSourceContent("database");
        result.setSourceNumber(sourceNumber);
        result.setTableErrors(errors);
        handler.sendTableMail(errors, result);
    }
    
    public void compareIndex() throws IOException {
        List<DifferenceIndex> errors = Lists.newArrayList();
        LinkedHashMap<String, TreeMap<String, Index>> source = sourseInfo.getIndexs();
        LinkedHashMap<String, TreeMap<String, Index>> compare = compareInfo.getIndexs();
      //起多少个线程
        CountDownLatch latch = new CountDownLatch(databases.size());
        
        for (Database database : databases) {
            String owner = database.getDbusr();
            CompareIndex diff = new CompareIndex(latch);
            diff.setCompare(compare.get(owner));
            diff.setSource(source.get(owner));
            diff.setOwner(owner);
            diff.setErrors(errors);
            diff.start();
        }
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        CompareResult result = new CompareResult();
        result.setCompareContent("database");
        result.setSourceContent("database");
        result.setIndexErrors(errors);
        handler.sendIndexMail(errors, result);
    }
    
}
