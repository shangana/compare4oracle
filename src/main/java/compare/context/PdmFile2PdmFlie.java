package compare.context;

import java.util.Map;

import org.dom4j.Element;

import compare.beans.Table;
import compare.beans.definition.PDM;
import compare.beans.definition.XMLConfig;
import compare.core.CompareResult;
import compare.core.MailHandler;

/**
 * @author   yueshanfei
 * @date  2016年9月5日
 */
public class PdmFile2PdmFlie extends Compare {
    private MailHandler handler;
    
    private PDM pdm;
    
    private PDM single;
    
    public PdmFile2PdmFlie(String config) {
        super(config);
        pdm = xmlConfig.getPdm();
        single = xmlConfig.getSinglePDM();
    }
    
    public CompareResult compareTable(Map<String, Table> arg1, Map<String, Table> arg2) {
        return null;
    }
    
    @Override
    protected boolean verifyXMLConfig() {
        return false;
    }
    
    @Override
    protected XMLConfig xmlConfigReader(Element root) {
        // TODO Auto-generated method stub
        return null;
    }
}
