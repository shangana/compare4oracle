

import org.junit.Test;

import compare.AppMain;

public class Pdm2DatabaseTest {
    
    @Test
    public void test() {
        String config="/Users/apple/asiainfo/08-DBCompare比对工具/pdm2oracledb.xml";
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
        String a="!";
        String b = null;
        System.out.println(a.equals(b));
    }
}
