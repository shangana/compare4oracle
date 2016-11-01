# compare4oracle 为oracle版本
pdm中无Owner属性则以文件(outhtml)或邮件的方式输出. 通过配置的xml比对用户,比对表和索引,比对完成后以文件(outhtml)或邮件的方式输出.

支持的模式有: 

* pdm文件与oracle数据库.
* oracle数据库与oracle数据库.

说明:
>
 * compare4oracle_fat.jar 是可执行文件
 * 参数说明 第一个参数:配置文件. 第二个参数:模式 1-pdm比对数据库 2-数据库比对数据库
 
执行pdm比对oracle数据库命令实例
```
 java -jar compare4oracle_fat.jar pdm2oracledb.xml 1
```