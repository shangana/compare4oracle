package compare.context;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import compare.beans.DatabaseInfo;
import compare.beans.PdmFileInfo;

/**
 * @author   yueshanfei
 * @date  2016年10月26日
 */
public class ThreadOwner extends Thread {
    protected static final Logger logger = LogManager.getLogger();
    
    private String owner;
    private CountDownLatch latch;
    private List<DifferenceTable> tableerrors;
    private List<DifferenceIndex> indexerrors;
    private boolean single;
    private PdmFileInfo pdmFileInfo;
    private DatabaseInfo databaseInfo;
    private boolean submeter;
    
    @Override
    public void run() {
        logger.debug(owner + " 用户执行开始...");
        final CountDownLatch comparelath = new CountDownLatch(2);
        ThreadTable tt = new ThreadTable();
        tt.setErrors(tableerrors);
        tt.setLatch(comparelath);
        tt.setOwner(owner);
        tt.setSingle(single);
        tt.setPdmFileInfo(pdmFileInfo);
        tt.setDatabaseInfo(databaseInfo);
        tt.setSubmeter(submeter);
        tt.start();
        ThreadIndex ti = new ThreadIndex();
        ti.setErrors(indexerrors);
        ti.setOwner(owner);
        ti.setLatch(comparelath);
        ti.setSingle(single);
        ti.setPdmFileInfo(pdmFileInfo);
        ti.setDatabaseInfo(databaseInfo);
        ti.setSubmeter(submeter);
        ti.start();
        try {
            comparelath.await();
            latch.countDown();
            logger.debug(owner + " 用户执行结束.");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    
    public List<DifferenceTable> getTableerrors() {
        return tableerrors;
    }
    
    public void setTableerrors(List<DifferenceTable> tableerrors) {
        this.tableerrors = tableerrors;
    }
    
    public List<DifferenceIndex> getIndexerrors() {
        return indexerrors;
    }
    
    public void setIndexerrors(List<DifferenceIndex> indexerrors) {
        this.indexerrors = indexerrors;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public void setPdmFileInfo(PdmFileInfo pdmFileInfo) {
        this.pdmFileInfo = pdmFileInfo;
    }

    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }

    public void setSubmeter(boolean submeter) {
        this.submeter = submeter;
    }
    
}
