package compare.context;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import compare.beans.PdmFileInfo;
import compare.beans.definition.Database;

/**
 * @author   yueshanfei
 * @date  2016年10月26日
 */
public class ThreadOwner extends Thread {
    protected static final Logger logger = LogManager.getLogger();
    private String owner;
    private CountDownLatch latch;
    private boolean single;
    private PdmFileInfo pdmFileInfo;
    private boolean submeter;
    private ThreadParam param;
    private OwnerParam ownerParam;
    
    @Override
    public void run() {
        logger.debug(owner + " This user execution starts...");
        Database database = ownerParam.getDatabase();
        if (null == database) {
            latch.countDown();
            logger.error(new Exception(owner + " 没有此配置的数据库连接."));
            return;
        }
        
        final CountDownLatch comparelath = new CountDownLatch(2);
        try {
            executeThread(comparelath);
            comparelath.await();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            ownerParam.closeConnection();
        }
        latch.countDown();
        logger.debug(owner + " This user is executed.");
    }

    private void executeThread(final CountDownLatch comparelath) {
        ThreadTable tt = new ThreadTable();
        tt.setParam(param);
        tt.setOwnerParam(ownerParam);
        tt.setLatch(comparelath);
        tt.setOwner(owner);
        tt.setSingle(single);
        tt.setPdmFileInfo(pdmFileInfo);
        tt.setSubmeter(submeter);
        tt.start();
        ThreadIndex ti = new ThreadIndex();
        ti.setParam(param);
        ti.setOwnerParam(ownerParam);
        ti.setOwner(owner);
        ti.setLatch(comparelath);
        ti.setSingle(single);
        ti.setPdmFileInfo(pdmFileInfo);
        ti.setSubmeter(submeter);
        ti.start();
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public CountDownLatch getLatch() {
        return latch;
    }
    
    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
    
    public void setSingle(boolean single) {
        this.single = single;
    }
    
    public void setPdmFileInfo(PdmFileInfo pdmFileInfo) {
        this.pdmFileInfo = pdmFileInfo;
    }
    
    public void setSubmeter(boolean submeter) {
        this.submeter = submeter;
    }
    
//    public void setDatabase(Database database) {
//        this.database = database;
//    }
//    
//    public void setSource(Database source) {
//        this.source = source;
//    }
    
    public ThreadParam getParam() {
        return param;
    }
    
    public void setParam(ThreadParam param) {
        this.param = param;
    }

    public void setOwnerParam(OwnerParam privateParam) {
        this.ownerParam = privateParam;
    }
    
}
