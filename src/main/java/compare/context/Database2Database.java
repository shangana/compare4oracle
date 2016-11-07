package compare.context;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.dom4j.Element;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import compare.beans.definition.Database;
import compare.beans.definition.Equality;
import compare.beans.definition.XMLConfig;
import compare.core.MailHandler;


public class Database2Database extends Compare {
    private MailHandler handler;
    private List<Database> sources;
    private List<Database> databases;
    private Database single;
    private Equality equality;
    ThreadParam param = new ThreadParam();
    
    public Database2Database(String config) {
        super(config);
        handler = new MailHandler(xmlConfig.getMail());
        sources = xmlConfig.getSources();
        equality = xmlConfig.getEquality();
        //
        if (null != single) {
          logger.debug("--single compare config--");
          databases = Lists.newArrayList(single);
      } else {
          logger.debug("--databases compare config--");
          databases = xmlConfig.getDatabases();
      }
        
        try {
            getEquality();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(new Exception("equality tag config is error,赶快去看看吧!"));
            return;
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
            else if ("sources".equals(nodeName)) {
                xml.setSources(item);
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
        List<String> usernames = equality.getUsernames();
        Map<String,Database> sourcemap = Maps.newHashMap();
        Map<String,Database> dbmap = Maps.newHashMap();
        if (null == usernames) {
            usernames = Lists.newArrayList();
            if (null != single) {
                for (Database source : sources) {
                    String user = source.getDbusr();
                    if (user.equalsIgnoreCase(single.getDbusr())) {
                        usernames.add("<=>");
                        dbmap.put(single.getDbusr().toUpperCase(), single);
                        sourcemap.put(user.toUpperCase(), source);
                        break;
                    }
                }
            }//此步有逻辑有问题
            else {
                for (Database source : sources) {
                    String user = source.getDbusr();
                    for (Database database : databases) {
                        String dbusr = database.getDbusr();
                        if (user.equalsIgnoreCase(dbusr)) {
                            usernames.add(dbusr + "=" + dbusr);
                            dbmap.put(dbusr.toUpperCase(), database);
                            sourcemap.put(user.toUpperCase(), source);
                            break;
                        }
                    }
                }
            }
            equality.setUsernames(usernames);
            equality.setDbmap(dbmap);
        }
       
    }
    
    public void compare() throws Exception {
        //
        verify();
        //
        List<DifferenceTable> tableerrors = Lists.newArrayList();
        List<DifferenceIndex> indexerrors = Lists.newArrayList();
        Integer countSourceTables = 0 ;
        Integer countCompareTables = 0 ;
        param.setIndexerrors(indexerrors);
        param.setTableerrors(tableerrors);
        param.setCountSourceTables(countSourceTables);
        param.setCountCompareTables(countCompareTables);
        List<String> usernames = equality.getUsernames();
        //起多少个线程
        CountDownLatch latch = new CountDownLatch(usernames.size());
        for (String owner : usernames) {
            String[] split = owner.split("=");
            if (("".equals(split[0])||"".equals(split[1])) && !split[0].equals(split[1])) {
                logger.error("比对双方的对应用户配置错误!");
                return;
            }
            OwnerParam ownerParam = new OwnerParam();
          //线程执行
            ThreadOwner threadOwner= new ThreadOwner();
            threadOwner.setLatch(latch);
            threadOwner.setOwner(owner);
            ownerParam.setDatabase(equality.getDbmap().get(split[1].toUpperCase()));
            ownerParam.setSource(equality.getSourcemap().get(split[0].toUpperCase()));
            threadOwner.setParam(param);
            threadOwner.setSingle(null != single);
            threadOwner.start();
        }
        
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
//        //
//        sendTableMail(tableerrors);
//        //
//        sendIndexMail(indexerrors);
        
    }
    
    private void verify() throws Exception {

        if (null == sources || null == databases) {
            throw new Exception("--This is xml config file is error.");
        }
    }

//    private void compareTable() throws IOException {
//
//        logger.debug("compare table count Thread number is "+databases.size());
//        List<DifferenceTable> errors = Lists.newArrayList();
//        LinkedHashMap<String, TreeMap<String, Table>> source = sourseInfo.getTables();
//        LinkedHashMap<String, TreeMap<String, Table>> compare = compareInfo.getTables();
//        //起多少个线程
//        CountDownLatch latch = new CountDownLatch(databases.size());
//        
//        for (Database database : databases) {
//            CompareTable diff = new CompareTable(latch);
//            if (compareInfo.isSingle()) {
//                TreeMap<String, Table> c = Maps.newTreeMap();
//                for (TreeMap<String, Table> treeMap : compare.values())
//                {
//                    c.putAll(treeMap);
//                }
//                diff.setCompare(c);
//                
//
//                TreeMap<String, Table> s = Maps.newTreeMap();
//                for (TreeMap<String, Table> treeMap : source.values())
//                {
//                    s.putAll(treeMap);
//                }
//                diff.setSource(s);
//                diff.setOwner("");
//            }
//            else {
//                String owner = database.getDbusr();
//                diff.setCompare(compare.get(owner));
//                diff.setSource(source.get(owner));
//                diff.setOwner(owner);
//            }
//            diff.setErrors(errors);
//            diff.start();
//        }
//        try {
//            latch.await();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        logger.debug("compare table is finished.");
//        int sourceNumber= source.values().size();
//        int compareNumber = compare.values().size();
//
//        CompareResult result = new CompareResult();
//        result.setCompareContent("database");
//        result.setCompareNumber(compareNumber);
//        result.setSourceContent("database");
//        result.setSourceNumber(sourceNumber);
//        result.setTableErrors(errors);
//        handler.sendTableMail(errors, result);
//    }
//    
//    private void compareIndex() throws IOException {
//        List<DifferenceIndex> errors = Lists.newArrayList();
//        LinkedHashMap<String, TreeMap<String, Index>> source = sourseInfo.getIndexs();
//        LinkedHashMap<String, TreeMap<String, Index>> compare = compareInfo.getIndexs();
//      //起多少个线程
//        CountDownLatch latch = new CountDownLatch(databases.size());
//        
//        for (Database database : databases) {
//            String owner = database.getDbusr();
//            CompareIndex diff = new CompareIndex(latch);
//            diff.setCompare(compare.get(owner));
//            diff.setSource(source.get(owner));
//            diff.setOwner(owner);
//            diff.setErrors(errors);
//            diff.start();
//        }
//        try {
//            latch.await();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        CompareResult result = new CompareResult();
//        result.setCompareContent("database");
//        result.setSourceContent("database");
//        result.setIndexErrors(errors);
//        handler.sendIndexMail(errors, result);
//    }
    
}
