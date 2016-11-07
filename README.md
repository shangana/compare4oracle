# compare4oracle 为oracle版本
pdm中无Owner属性则以文件(outhtml)或邮件的方式输出. 通过配置的xml比对用户,比对表和索引,比对完成后以文件(outhtml)或邮件的方式输出.

支持的模式有: 
>
* pdm文件与oracle数据库.
* oracle数据库与oracle数据库.

文件说明, 这些文件必须放在同一目录下:
>
 * compare4oracle_fat.jar 是可执行文件
 * 参数说明 第一个参数:配置文件. 第二个参数:模式 1-pdm比对数据库 2-数据库比对数据库
 * pdm2oracledb.xml 是pdm与oracle数据库之间比对的配置文件
   一般是pdm文件与测试库比对
 * oracledb2oracledb.xml 是oracle数据库与oracle数据库之间比对的配置文件
   一般是测试库与正式库比对使用
 * ErrorInformation.html 是PDM文件中表无Owner属性的模板文件
 * compare_table.html 是比对表的模板文件
 * compare_index.html 是比对索引的模板文件
 
执行pdm比对oracle数据库命令实例
```
 java -jar compare4oracle_fat.jar pdm2oracledb.xml 1
```

执行结果生成outhtml目录,若配置邮件,结果会发送邮件通知.

问题解答:
>
 1.解决pdm无法使用XML解析
  答:这种问题是由于pdm是bin二进制格式,需要人工另存为xml格式的pdm.
 2.