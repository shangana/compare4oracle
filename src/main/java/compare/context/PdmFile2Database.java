package compare.context;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.dom4j.Element;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import compare.beans.PdmFileInfo;
import compare.beans.Table;
import compare.beans.definition.Database;
import compare.beans.definition.Equality;
import compare.beans.definition.PDM;
import compare.beans.definition.PDM.Local;
import compare.beans.definition.PDM.SVN;
import compare.beans.definition.XMLConfig;
import compare.core.CompareResult;
import compare.core.MailHandler;

public class PdmFile2Database extends Compare {
    private PdmFileCompare pdmCompare = PdmFileCompare.getInstance();
//    private DatabaseCompare databaseCompare = DatabaseCompare.getInstance();
    private PdmFileInfo pdmFileInfo;
//    private DatabaseInfo databaseInfo;
    private MailHandler handler;
    private PDM pdm;
    private List<Database> databases;
    private Database single;
    private Equality equality;

    public PdmFile2Database(String config) {
        super(config);
        pdm = xmlConfig.getPdm();
        handler = new MailHandler(xmlConfig.getMail());
        equality = xmlConfig.getEquality();
        
        single = xmlConfig.getSingleDatabase();
        try {
            pdmFileInfo = pdmCompare.getPdmFileInfo(pdm);
        }
        catch (Exception e1) {
            e1.printStackTrace();
            logger.error(new Exception("get pdm file is error,赶快去看看吧!"));
            return;
        }
        
        if (null != single) {
            logger.debug("--single compare config--");
            databases = Lists.newArrayList(single);
        }
        else {
            logger.debug("--databases compare config--");
            databases = xmlConfig.getDatabases();
        }
        try {
            getEquality();
        }
        catch (Exception e2) {
            e2.printStackTrace();
            logger.error(new Exception("equality tag config is error,赶快去看看吧!"));
            return;
        }
        //pdm 文件中无owner属性的发邮件通知
        try {
            pdmFileNoOwner();
        }
        catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }
    
    private void pdmFileNoOwner() throws Exception {
        LinkedHashMap<String, TreeMap<String, Table>> source = pdmFileInfo.getTables();
        TreeMap<String, Table> map = source.get(null);
        if (null == map) {
            logger.warn("--没有无Owner的表--");
            return;
        }
        List<NoOwner> nws = Lists.newArrayList();
        Iterator<Entry<String, Table>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Table> entry = iterator.next();
            Table value = entry.getValue();
            NoOwner no = new NoOwner();
            no.setPath(value.getPdmfilepath());
            no.setTableName(value.getTableName());
            nws.add(no);
        }
        //send mail no owner 
        handler.sendNoOwnerMail(nws);
    }
    private void verify() throws Exception {
        if (null == pdmFileInfo || null == databases) {
            throw new Exception("--This is xml config file is error.");
        }
    }
    private void getEquality() throws Exception {
        List<String> usernames = equality.getUsernames();
        Map<String,Database> dbmap = Maps.newHashMap();
        if (null == usernames) {
            usernames = Lists.newArrayList();
            List<String> users = pdmFileInfo.getUsers();
            if (null != single) {
                usernames.add("<=>");
                dbmap.put(single.getDbusr().toUpperCase(), single);
            }
            else {
                for (Database database : databases) {
                    String dbusr = database.getDbusr();
                    if (users.contains(dbusr)) {
                        usernames.add(dbusr+"="+dbusr);
                        dbmap.put(dbusr.toUpperCase(), database);
                    }
                }
            }
            equality.setUsernames(usernames);
            equality.setDbmap(dbmap);
        }
        else {
            for (Database database : databases) {
                dbmap.put(database.getDbusr().toUpperCase(), database);
            }
            equality.setDbmap(dbmap);
        }
        if (usernames.isEmpty()) {
            logger.error(new Exception("没有比对的用户"));
            throw new Exception("没有比对的用户");
        }
        
    }
    public void compare() throws Exception {
        //
        verify();
        
        List<DifferenceTable> tableerrors = Lists.newArrayList();
        List<DifferenceIndex> indexerrors = Lists.newArrayList();
        ThreadParam param = new ThreadParam();
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
                logger.error(new Exception("比对双方的对应用户配置错误!"));
                return;
            }
            Database database = equality.getDbmap().get(split[1].toUpperCase());
            if (null == database)  {
                logger.error(new Exception("<equality> config error, 比对双方的对应用户与数据连接不一致."));
                latch.countDown();
                continue;
            }
            OwnerParam ownerParam = new OwnerParam();
            //线程执行
            ThreadOwner threadOwner= new ThreadOwner();
            threadOwner.setLatch(latch);
            threadOwner.setOwner(owner);
            threadOwner.setPdmFileInfo(pdmFileInfo);
            ownerParam.setDatabase(database);
            threadOwner.setParam(param);
            threadOwner.setOwnerParam(ownerParam);
            threadOwner.setSingle(null != single);
            threadOwner.setSubmeter(pdm.isSubmeter());
            threadOwner.start();
        }
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        sendTableMail(tableerrors, param);
        //
        sendIndexMail(indexerrors);
    }

    private void sendIndexMail(List<DifferenceIndex> indexerrors) throws IOException {
        CompareResult result = new CompareResult();
        result.setCompareContent("database");
        result.setSourceContent("pdm");
        result.setIndexErrors(indexerrors);
        handler.sendIndexMail(indexerrors, result);
    }

    private void sendTableMail(List<DifferenceTable> errors, ThreadParam param) throws IOException {
        //
        int sourceNumber = param.getCountSourceTables();
        int compareNumber = param.getCountCompareTables();
        
        CompareResult result = new CompareResult();
        result.setCompareContent("database");
        result.setCompareNumber(compareNumber);
        result.setSourceContent("pdm");
        result.setSourceNumber(sourceNumber);
        result.setTableErrors(errors);
        handler.sendTableMail(errors, result);
    }
//    public void compareTable() throws IOException {
//        List<DifferenceTable> errors = Lists.newArrayList();
//        LinkedHashMap<String, TreeMap<String, Table>> source = pdmFileInfo.getTables();
//        LinkedHashMap<String, TreeMap<String, Table>> compare = databaseInfo.getTables();
//        //起多少个线程
//        CountDownLatch latch = new CountDownLatch(databases.size());
//        
//        for (Database database : databases) {
//            CompareTable diff = new CompareTable(latch);
//            if (databaseInfo.isSingle()) {
//                TreeMap<String, Table> c = Maps.newTreeMap();
//                for (TreeMap<String, Table> treeMap : compare.values()) {
//                    c.putAll(treeMap);
//                }
//                diff.setCompare(c);
//                
//                TreeMap<String, Table> s = Maps.newTreeMap();
//                for (TreeMap<String, Table> treeMap : source.values()) {
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
//            diff.isSubmeter(pdm.isSubmeter());
//            diff.start();
//        }
//        try {
//            latch.await();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        int sourceNumber = source.values().size();
//        int compareNumber = compare.values().size();
//        
//        CompareResult result = new CompareResult();
//        result.setCompareContent("database");
//        result.setCompareNumber(compareNumber);
//        result.setSourceContent("pdm");
//        result.setSourceNumber(sourceNumber);
//        result.setTableErrors(errors);
//        handler.sendTableMail(errors, result);
//    }
//    
//    public void compareIndex() throws IOException {
//        List<DifferenceIndex> errors = Lists.newArrayList();
//        LinkedHashMap<String, TreeMap<String, Index>> source = pdmFileInfo.getIndexs();
//        LinkedHashMap<String, TreeMap<String, Index>> compare = databaseInfo.getIndexs();
//        //起多少个线程
//        CountDownLatch latch = new CountDownLatch(databases.size());
//        
//        for (Database database : databases) {
//            String owner = database.getDbusr();
//            source.get(owner);
//            CompareIndex diff = new CompareIndex(latch);
//            diff.setCompare(compare.get(owner));
//            diff.setSource(source.get(owner));
//            diff.setOwner(owner);
//            diff.setErrors(errors);
//            diff.isSubmeter(pdm.isSubmeter());
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
//        result.setSourceContent("pdm");
//        result.setIndexErrors(errors);
//        handler.sendIndexMail(errors, result);
//    }
//    
    protected boolean verifyXMLConfig() {
        PDM pdmconfig = xmlConfig.getPdm();
        List<Database> databases = xmlConfig.getDatabases();
        if (null == pdmconfig || null == databases) {
            logger.error(xmlConfig + ",this config is error!");
            return false;
        }
        Local local = pdmconfig.getLocal();
        if (null != local) {
            SVN svn = pdmconfig.getSvn();
            //两者都配置,则选择local配置
            if (null != svn) {
                logger.warn("Local configuration and SVN configuration are configured, then take the local configuration");
            }
        }
        else {
            SVN svn = pdmconfig.getSvn();
            if (null == svn) {
                logger.error("pdm svn config is error.");
                return false;
            }
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
            else if ("pdm".equals(nodeName)) {
                xml.setPdm(item);
            }
            else if ("databases".equals(nodeName)) {
                xml.setDatabases(item);
            }
            else if ("single".equals(nodeName)) {
                xml.setSingle(item);
            }
            else if ("equality".equals(nodeName)) {
                xml.setEquality(item);
            }
        }
        return xml;
    }
    
}
