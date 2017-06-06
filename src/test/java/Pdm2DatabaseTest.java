

import org.junit.Test;

import compare.AppMain;
import compare.core.MailHandler;

public class Pdm2DatabaseTest {
    
    @Test
    public void test() {
        String config="/Users/apple/asiainfo/trunk/compare4oracle/pdm2oracledb.xml";
        String[] args = {config,"1"};
        AppMain.main(args);
    }
    @Test
    public void testOracle() {
        String config="/Users/apple/asiainfo/08-DBCompare比对工具/oracledb2oracledb.xml";
        String[] args = {config,"2"};
        AppMain.main(args);
    }
    
    @Test
    public void test11() {
        String fileName="/Users/apple/asiainfo/trunk/compare4oracle/compare_table.html";
        try {
			System.out.println(MailHandler.getCharsetName(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
